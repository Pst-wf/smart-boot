package com.smart.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Http请求工具
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/4/19
 */
@Slf4j
public class HttpUtil {

    /**
     * Post请求
     *
     * @param url     请求地址
     * @param body    请求参数
     * @param headers 请求头
     * @return String
     */
    public static String postJson(String url, String body, Map<String, String> headers) {
        return post(url, body, headers, "application/json");
    }

    /**
     * Post请求
     *
     * @param url  请求地址
     * @param body 请求参数
     * @return String
     */
    public static String postJson(String url, String body) {
        return post(url, body, null, "application/json");
    }

    /**
     * Post请求
     *
     * @param url         请求地址
     * @param body        请求参数
     * @param headers     请求头
     * @param contentType contentType
     * @return String
     */
    public static String post(String url, String body, Map<String, String> headers, String contentType) {
        if (StringUtil.isBlank(url)) {
            throw new RuntimeException("请求地址不能为空！");
        }
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        log.error("============================ 发送请求 {} ============================", time);
        log.info("URL - {}", url);
        HttpRequest request = HttpRequest.post(url);
        // 追加contentType
        if (StringUtil.isNotBlank(contentType)) {
            request = request.contentType(contentType);
            log.info("content-type - {}", contentType);
        }
        // 追加请求头
        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                request = request.header(key, value);
                log.info("header - {}:{}", key, value);
            }
        }
        if (StringUtil.isNotBlank(body)) {
            request = request.body(body);
            log.info("参数 - {}", body);
        }
        // 追加请求体
        HttpResponse response = request.execute();
        if (!response.isOk()) {
            log.error("发送请求失败 - {}", response.body());
            throw new RuntimeException("发送请求失败！");
        }
        log.info("发送请求成功 - {}", response.body());
        return response.body();
    }

    /**
     * Get请求
     *
     * @param url 请求地址
     * @return String
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * Get请求
     *
     * @param url  请求地址
     * @param body 参数
     * @return String
     */
    public static String get(String url, String body) {
        return get(url, body, null);
    }

    /**
     * Get请求
     *
     * @param url     请求地址
     * @param body    参数
     * @param headers 请求头
     * @return String
     */
    public static String get(String url, String body, Map<String, String> headers) {
        return get(url, body, headers, null);
    }

    /**
     * Get请求
     *
     * @param url         请求地址
     * @param body        参数
     * @param headers     请求头
     * @param contentType contentType
     * @return String
     */
    public static String get(String url, String body, Map<String, String> headers, String contentType) {
        if (StringUtil.isBlank(url)) {
            throw new RuntimeException("请求地址不能为空！");
        }
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        log.error("============================ 发送请求 {} ============================", time);
        if (StringUtil.isNotBlank(body)) {
            log.info("参数 - {}", body);
            StringBuilder sb = new StringBuilder();
            JSONObject params = JSON.parseObject(body);
            params.keySet().forEach(x -> {
                sb.append(x).append("=").append(params.get(x)).append("&");
            });
            if (sb.length() > 0) {
                url = url + "?" + sb.substring(0, sb.length() - 1);
            }
        }
        log.info("URL - {}", url);
        HttpRequest request = HttpRequest.get(url);
        // 追加contentType
        if (StringUtil.isNotBlank(contentType)) {
            request = request.contentType(contentType);
            log.info("content-type - {}", contentType);
        }
        // 追加请求头
        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                request = request.header(key, value);
                log.info("header - {}:{}", key, value);
            }
        }
        // 追加请求体
        HttpResponse response = request.execute();
        if (!response.isOk()) {
            log.error("发送请求失败 - {}", response.body());
            throw new RuntimeException("发送请求失败！");
        }
        log.info("发送请求成功 - {}", response.body());
        return response.body();
    }
}
