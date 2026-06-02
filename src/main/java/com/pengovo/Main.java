package com.pengovo;


import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.pengovo.utils.ConfigUtils;
import com.pengovo.utils.DnsUtils;
import com.pengovo.utils.IPUtils;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                    try {
                        interval = Integer.valueOf(args[i + 1]);
                    } catch (NumberFormatException e) {
                        logAndExit("-interval参数不合法，应输入大于0的整数");
                    }
                    if (interval <= 0) {
                        logAndExit("-interval参数不合法，应输入大于0的整数");
                    }
                    i++; // 跳过下一个参数，因为它是分钟数
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
                log.error("DDNS更新失败，本轮跳过，等待下次执行", e);
            }
        }, 0, interval, TimeUnit.MINUTES);
    }

    // 执行更新操作
    private static void update() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        log.info("-------------------开始执行更新操作，当前时间为：{}--------------------", now);
        currentHostIP = IPUtils.getOutIPV4();
        log.info("-------------------当前公网ip为：{}--------------------", currentHostIP);
        String regionId = ConfigUtils.getRequiredString(ConfigUtils.getClientConfigs(), "regionId");
        List<Map<String, Object>> domainConfigs = ConfigUtils.getDomainConfigs();

        for (Map<String, Object> domainConfig : domainConfigs) {
            handleDomainUpdate(regionId, domainConfig);
        }
    }

    // 处理单个域名更新
    private static void handleDomainUpdate(String regionId, Map<String, Object> domainConfig) throws Exception {
        String domainName = ConfigUtils.getRequiredString(domainConfig, "domainName");
        String RR = ConfigUtils.getRequiredString(domainConfig, "RR");
        String recordType = ConfigUtils.getRequiredString(domainConfig, "recordType");

        log.info("正在更新解析配置，domainName={}, RR={}, recordType={}", domainName, RR, recordType);
        com.aliyun.alidns20150109.Client client = DnsUtils.Initialization(regionId);
        DescribeDomainRecordsResponse resp = DnsUtils.DescribeDomainRecords(client, domainName, RR, recordType);
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord record = findMatchedRecord(resp, RR, recordType);
        if (record == null) {
            log.error("未找到匹配的解析记录，domainName={}, RR={}, recordType={}", domainName, RR, recordType);
            return;
        }
        // 记录ID
        String recordId = record.recordId;
        // 记录值
        String recordsValue = record.value;
        if (!currentHostIP.equals(recordsValue)) {
            // 修改解析记录
            UpdateDomainRecordRequest req = buildUpdateRequest(RR, recordId, currentHostIP, recordType);
            DnsUtils.UpdateDomainRecord(client, req);
            log.info("解析记录已更新，domainName={}, RR={}, oldIP={}, newIP={}", domainName, RR, recordsValue, currentHostIP);
        } else {
            log.info("当前解析记录已是最新的了，domainName={}, RR={}, ip={}", domainName, RR, currentHostIP);
        }
    }

    private static DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord findMatchedRecord(DescribeDomainRecordsResponse resp, String RR, String recordType) {
        if (resp == null || resp.body == null || resp.body.domainRecords == null || resp.body.domainRecords.record == null) {
            return null;
        }
        return resp.body.domainRecords.record.stream()
                .filter(record -> Objects.equals(RR, record.RR) && Objects.equals(recordType, record.type))
                .findFirst()
                .orElse(null);
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