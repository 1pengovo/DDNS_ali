package com.pengovo.utils;


import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.alidns20150109.models.UpdateDomainRecordResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;


public class DnsUtils {


    /**
     * Initialization  初始化公共请求参数
     */
    public static com.aliyun.alidns20150109.Client Initialization(String regionId) throws Exception {
        Config config = new Config();
        // 您的AccessKey ID
        config.accessKeyId = (String) ConfigUtils.getConfig().get("accessKeyId");
        // 您的AccessKey Secret
        config.accessKeySecret = (String) ConfigUtils.getConfig().get("accessKeySecret");
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
            com.aliyun.teaconsole.Client.log("-------------------获取主域名的所有解析记录列表--------------------");
            com.aliyun.teaconsole.Client.log(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(resp)));
            return resp;
        } catch (TeaException error) {
            com.aliyun.teaconsole.Client.log(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teaconsole.Client.log(error.message);
        }
        return null;
    }

    /**
     * 修改解析记录
     */
    public static void UpdateDomainRecord(com.aliyun.alidns20150109.Client client, UpdateDomainRecordRequest req) throws Exception {
        try {
            UpdateDomainRecordResponse resp = client.updateDomainRecord(req);
            com.aliyun.teaconsole.Client.log("-------------------修改解析记录--------------------");
            com.aliyun.teaconsole.Client.log(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(resp)));
            com.aliyun.teaconsole.Client.log("修改解析记录成功");
        } catch (TeaException error) {
            com.aliyun.teaconsole.Client.log(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teaconsole.Client.log(error.message);
        }
    }

}
