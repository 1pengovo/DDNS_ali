package com.pengovo;


import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.tea.TeaModel;
import com.pengovo.utils.ConfigUtils;
import com.pengovo.utils.DnsUtils;
import com.pengovo.utils.IPUtils;

public class Main {
    public static void main(String[] args) throws Exception {
        update();
    }

    public static void update() throws Exception {
        String regionId = (String) ConfigUtils.getConfig().get("regionId");
        String domainName = (String) ConfigUtils.getConfig().get("domainName");
        String RR = (String) ConfigUtils.getConfig().get("RR");
        String recordType = (String) ConfigUtils.getConfig().get("recordType");
        String currentHostIP = IPUtils.getOutIPV4();
        com.aliyun.alidns20150109.Client client = DnsUtils.Initialization(regionId);
        DescribeDomainRecordsResponse resp = DnsUtils.DescribeDomainRecords(client, domainName, RR, recordType);
        if (com.aliyun.teautil.Common.isUnset(TeaModel.buildMap(resp)) || com.aliyun.teautil.Common.isUnset(TeaModel.buildMap(resp.body.domainRecords.record.get(0)))) {
            com.aliyun.teaconsole.Client.log("错误参数！");
            return;
        }

        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord record = resp.body.domainRecords.record.get(0);
        // 记录ID
        String recordId = record.recordId;
        // 记录值
        String recordsValue = record.value;
        com.aliyun.teaconsole.Client.log("-------------------当前主机公网IP为：" + currentHostIP + "--------------------");
        if (!com.aliyun.teautil.Common.equalString(currentHostIP, recordsValue)) {
            // 修改解析记录
            UpdateDomainRecordRequest req = new UpdateDomainRecordRequest();
            // 主机记录
            req.RR = RR;
            // 记录ID
            req.recordId = recordId;
            // 将主机记录值改为当前主机IP
            req.value = currentHostIP;
            // 解析记录类型
            req.type = recordType;
            DnsUtils.UpdateDomainRecord(client, req);
        } else {
            com.aliyun.teaconsole.Client.log("当前解析记录已是最新的了");
        }
    }
}
