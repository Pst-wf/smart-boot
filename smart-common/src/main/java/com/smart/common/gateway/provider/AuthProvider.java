package com.smart.common.gateway.provider;

import java.util.ArrayList;
import java.util.List;

/**
 * 鉴权过滤
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public class AuthProvider {
    public static final String TARGET = "/**";
    public static final String REPLACEMENT = "";
    /**
     * 全部跳过验证的接口地址
     */
    private static final List<String> All_SKIP_URL = new ArrayList<>();
    /**
     * 跳过验证的接口地址
     */
    private static final List<String> DEFAULT_SKIP_URL = new ArrayList<>();

    /**
     * 跳过打印的接口地址
     */
    private static final List<String> SKIP_PRINT_URL = new ArrayList<>();

    /*
      此处添加需跳过鉴权的路由
     */
    static {
        // 鉴权忽略
        systemSkip();
        oauthSkip();
        oauthFrontSkip();
        businessSkip();
        // 打印忽略
        printSkip();
    }

    /**
     * 默认无需鉴权的API
     *
     * @return List
     */
    public static List<String> getDefaultSkipUrl() {
        return DEFAULT_SKIP_URL;
    }

    /**
     * 初始化全部无需鉴权的API
     *
     * @return List
     */
    public static List<String> initAllSkipUrl(List<String> urls) {
        All_SKIP_URL.addAll(urls);
        return All_SKIP_URL;
    }

    /**
     * 全部无需鉴权的API
     *
     * @return List
     */
    public static List<String> getAllSkipUrl() {
        return All_SKIP_URL;
    }

    /**
     * 系统 忽略鉴权接口
     */
    public static void systemSkip() {
        DEFAULT_SKIP_URL.add("/example");
        //文件上传
        DEFAULT_SKIP_URL.add("/file/upload");
        DEFAULT_SKIP_URL.add("/file/download");
        DEFAULT_SKIP_URL.add("/file/online");
        DEFAULT_SKIP_URL.add("/file/callback");
        DEFAULT_SKIP_URL.add("/front/**");
        DEFAULT_SKIP_URL.add("/druid/**");
        // xxl job 回调
        DEFAULT_SKIP_URL.add("/api/**");
        // websocket
        DEFAULT_SKIP_URL.add("/ws/**");
    }

    /**
     * OAUTH 忽略鉴权接口
     */
    public static void oauthSkip() {
        DEFAULT_SKIP_URL.add("/oauth/token");
        DEFAULT_SKIP_URL.add("/route/getConstantRoutes");
        DEFAULT_SKIP_URL.add("/oauth/captcha");
    }

    /**
     * OAUTH_FRONT 忽略鉴权接口
     */
    public static void oauthFrontSkip() {
        DEFAULT_SKIP_URL.add("/front/oauth/token");
    }

    /**
     * 业务 忽略鉴权接口
     */
    public static void businessSkip() {
    }

    /**
     * 忽略打印
     */
    public static void printSkip() {
        // xxl 回调
        SKIP_PRINT_URL.add("api");
        // druid连接池
        SKIP_PRINT_URL.add("druid");
    }

    /**
     * 默认无需鉴权的API
     *
     * @return List
     */
    public static String[] getSkipPrintUrl() {
        return SKIP_PRINT_URL.toArray(new String[]{});
    }
}
