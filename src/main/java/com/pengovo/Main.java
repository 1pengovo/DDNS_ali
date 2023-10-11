package com.pengovo;


import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.tea.TeaModel;
import com.pengovo.utils.ConfigUtils;
import com.pengovo.utils.DnsUtils;
import com.pengovo.utils.IPUtils;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Main {

    private static Integer interval = null;
    private static String currentHostIP = null;

    public static void main(String[] args) throws Exception {
        parseArguments(args);

        if (interval != null) {
            scheduleUpdateTask();
        } else {
            update();
        }
    }

    // 解析命令行参数
    private static void parseArguments(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-interval")) {
                if (i + 1 < args.length) {
                    interval = Integer.valueOf(args[i + 1]);
                    if (interval <= 0) {
                        logAndExit("-interval参数不合法，应输入大于0的整数");
                    }
                    i++; // 跳过下一个参数，因为它是文件名
                } else {
                    logAndExit("-interval后缺失参数");
                }
            }
        }
    }

    // 定期执行更新任务
    private static void scheduleUpdateTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 初始延迟为0秒，然后每隔多少分钟执行一次
        scheduler.scheduleAtFixedRate(() -> {
            try {
                update();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 0, interval, TimeUnit.MINUTES);
    }

    // 执行更新操作
    private static void update() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        log.info("-------------------开始执行更新操作，当前时间为：" + now + "--------------------");
        currentHostIP = IPUtils.getOutIPV4();
        log.info("-------------------当前公网ip为：" + currentHostIP + "--------------------");
        String regionId = (String) ConfigUtils.getClientConfigs().get("regionId");
        List<Map<String, Object>> domainConfigs = ConfigUtils.getDomainConfigs();

        if (domainConfigs != null) {
            for (Map<String, Object> domainConfig : domainConfigs) {
                handleDomainUpdate(regionId, domainConfig);
            }
        }
    }

    // 处理单个域名更新
    private static void handleDomainUpdate(String regionId, Map<String, Object> domainConfig) throws Exception {
        String domainName = (String) domainConfig.get("domainName");
        String RR = (String) domainConfig.get("RR");
        String recordType = (String) domainConfig.get("recordType");

        log.info("-------------------正在更新以下配置--------------------");
        log.info("Domain Name: " + domainName);
        log.info("RR: " + RR);
        log.info("Record Type: " + recordType);
        log.info("-----------------------------------------------");
        com.aliyun.alidns20150109.Client client = DnsUtils.Initialization(regionId);
        DescribeDomainRecordsResponse resp = DnsUtils.DescribeDomainRecords(client, domainName, RR, recordType);
        if (com.aliyun.teautil.Common.isUnset(TeaModel.buildMap(resp)) || com.aliyun.teautil.Common.isUnset(TeaModel.buildMap(resp.body.domainRecords.record.get(0)))) {
            log.error("错误参数！");
            return;
        }
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord record = resp.body.domainRecords.record.get(0);
        // 记录ID
        String recordId = record.recordId;
        // 记录值
        String recordsValue = record.value;
        if (!currentHostIP.equals(recordsValue)) {
            // 修改解析记录
            UpdateDomainRecordRequest req = buildUpdateRequest(RR, recordId, currentHostIP, recordType);
            DnsUtils.UpdateDomainRecord(client, req);
        } else {
            log.info("当前解析记录已是最新的了");
        }
    }

    private static UpdateDomainRecordRequest buildUpdateRequest(String RR, String recordId, String currentHostIP, String recordType) {
        UpdateDomainRecordRequest req = new UpdateDomainRecordRequest();
        // 主机记录
        req.RR = RR;
        // 记录ID
        req.recordId = recordId;
        // 将主机记录值改为当前主机IP
        req.value = currentHostIP;
        // 解析记录类型
        req.type = recordType;
        return req;
    }

    // 日志输出并退出程序
    private static void logAndExit(String message) throws Exception {
        log.error(message);
        System.exit(1);
    }
}
