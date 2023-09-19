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


    /**
     * 读取客户端配置
     */
    public static Map<String, Object> getClientConfigs() {
        return readYamlConfig();
    }

    ;

    /**
     * 读取域名配置
     */
    public static List<Map<String, Object>> getDomainConfigs() {
        Map<String, Object> config = readYamlConfig();
        if (config != null) {
            return (List<Map<String, Object>>) config.get("domains");
        }
        return null;
    }

    /**
     * 通过config.yml读取配置
     */
    public static Map<String, Object> readYamlConfig() {
        try {
            String jarPath = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarDirectory = new File(jarPath).getParent();
            String configFilePath = jarDirectory + File.separator + "config.yml";

            InputStream input = new FileInputStream(configFilePath);
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(input);
            input.close();

            return yamlData;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}
