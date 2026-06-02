package com.pengovo.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    private static Map<String, Object> cachedConfig;

    /**
     * 读取客户端配置
     */
    public static Map<String, Object> getClientConfigs() {
        return readYamlConfig();
    }

    /**
     * 读取域名配置
     */
    public static List<Map<String, Object>> getDomainConfigs() {
        Map<String, Object> config = readYamlConfig();
        return (List<Map<String, Object>>) config.get("domains");
    }

    public static String getRequiredString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new IllegalArgumentException("config.yml缺少必要配置：" + key);
        }
        return ((String) value).trim();
    }

    public static String getAccessKeyId() {
        return getSecretConfig(readYamlConfig(), "ALIYUN_ACCESS_KEY_ID", "accessKeyId");
    }

    public static String getAccessKeySecret() {
        return getSecretConfig(readYamlConfig(), "ALIYUN_ACCESS_KEY_SECRET", "accessKeySecret");
    }

    /**
     * 通过config.yml读取配置
     */
    public static Map<String, Object> readYamlConfig() {
        if (cachedConfig != null) {
            return cachedConfig;
        }

        String configFilePath = getConfigFilePath();
        try (InputStream input = new FileInputStream(configFilePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(input);
            if (yamlData == null) {
                throw new IllegalArgumentException("config.yml内容为空");
            }
            validateConfig(yamlData);
            cachedConfig = yamlData;
            return cachedConfig;
        } catch (IOException e) {
            throw new IllegalStateException("读取config.yml失败：" + configFilePath, e);
        }
    }

    private static void validateConfig(Map<String, Object> config) {
        getRequiredString(config, "regionId");
        getSecretConfig(config, "ALIYUN_ACCESS_KEY_ID", "accessKeyId");
        getSecretConfig(config, "ALIYUN_ACCESS_KEY_SECRET", "accessKeySecret");

        Object domains = config.get("domains");
        if (!(domains instanceof List) || ((List<?>) domains).isEmpty()) {
            throw new IllegalArgumentException("config.yml缺少必要配置：domains");
        }
    }

    private static String getSecretConfig(Map<String, Object> config, String envKey, String configKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return getRequiredString(config, configKey);
    }

    private static String getConfigFilePath() {
        try {
            String jarPath = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarDirectory = new File(jarPath).getParent();
            return jarDirectory + File.separator + "config.yml";
        } catch (URISyntaxException e) {
            throw new IllegalStateException("获取程序目录失败", e);
        }
    }

}