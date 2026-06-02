package com.pengovo.utils;


import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.alidns20150109.models.UpdateDomainRecordResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DnsUtils {


    /**
     * Initialization  初始化公共请求参数
     */
    public static com.aliyun.alidns20150109.Client Initialization(String regionId) throws Exception {
        Config config = new Config();
        // 您的AccessKey ID
        config.accessKeyId = ConfigUtils.getAccessKeyId();
        // 您的AccessKey Secret
        config.accessKeySecret = ConfigUtils.getAccessKeySecret();
        // 您的可用区ID
        config.regionId = regionId;
        return new com.aliyun.alidns20150109.Client(config);
    }

    /**
     * 获取主域名的所有解析记录列表
     */
    public static DescribeDomainRecordsResponse DescribeDomainRecords(com.aliyun.alidns20150109.Client client, String domainName, String RR, String recordType) throws Exception {
        DescribeDomainRecordsRequest req = new DescribeDomainRecordsRequest();
        // 主域名
        req.domainName = domainName;
        // 主机记录
        req.RRKeyWord = RR;
        // 解析记录类型
        req.type = recordType;
        try {
            DescribeDomainRecordsResponse resp = client.describeDomainRecords(req);
            log.info("获取解析记录成功，domainName={}, RR={}, recordType={}", domainName, RR, recordType);
            return resp;
        } catch (TeaException error) {
            log.error("获取解析记录失败：{}", error.message, error);
            throw error;
        } catch (Exception error) {
            log.error("获取解析记录失败", error);
            throw error;
        }
    }

    /**
     * 修改解析记录
     */
    public static void UpdateDomainRecord(com.aliyun.alidns20150109.Client client, UpdateDomainRecordRequest req) throws Exception {
        try {
            UpdateDomainRecordResponse resp = client.updateDomainRecord(req);
            log.info("修改解析记录成功，requestId={}", resp.body.requestId);
        } catch (TeaException error) {
            log.error("修改解析记录失败：{}", error.message, error);
            throw error;
        } catch (Exception error) {
            log.error("修改解析记录失败", error);
            throw error;
        }
    }

}