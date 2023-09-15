package com.pengovo.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigUtils {


    /**
     * 通过config.yml读取配置
     */
    public static Map<String, Object> getConfig() {
        // 读取YAML文件
        InputStream input = ConfigUtils.class.getResourceAsStream("/config.yml");
        if (input != null) {
            Yaml yaml = new Yaml();
            return yaml.load(input);
        } else {
            System.err.println("无法找到配置文件 config.yml");
            return null;
        }
    }

}
