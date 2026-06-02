package com.pengovo.utils;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

@Log4j2
public class IPUtils {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$");
    private static final List<String> IPV4_SERVICES = List.of(
            "https://api.ipify.org",
            "https://checkip.amazonaws.com",
            "https://ifconfig.me/ip"
    );

    /**
     * 获取外网IPv4
     */
    public static String getOutIPV4() {
        for (String serviceUrl : IPV4_SERVICES) {
            try {
                String ip = requestIp(serviceUrl);
                if (isValidIPv4(ip)) {
                    return ip;
                }
                log.warn("公网IP服务返回结果无效，serviceUrl={}, response={}", serviceUrl, ip);
            } catch (IOException e) {
                log.warn("公网IP服务请求失败，serviceUrl={}", serviceUrl, e);
            }
        }
        throw new IllegalStateException("无法获取公网IPv4，请检查网络或公网IP服务是否可用");
    }

    private static String requestIp(String serviceUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(serviceUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            urlConnection.setReadTimeout(READ_TIMEOUT_MS);
            urlConnection.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                return in.readLine() == null ? "" : in.readLine().trim();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static boolean isValidIPv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip.trim()).matches();
    }

}