package com.pengovo.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class ConfigUtils {


    /**
     * 通过config.yml读取配置
     */
    public static Map<String, Object> getConfig() {
        // 读取YAML文件
        try {
            String jarPath = ConfigUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarDirectory = new File(jarPath).getParent();
            String configFilePath = jarDirectory + File.separator + "config.yml";

            // 读取YAML文件
            InputStream input = new FileInputStream(configFilePath);
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(input);
            // 关闭输入流
            input.close();
            return yamlData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
