package com.smart.gateway.print;

import lombok.extern.slf4j.Slf4j;

/**
 * 打印处理器
 *
 * @author wf
 * @version 1.0.0
 * @since 2022/8/30
 */
@Slf4j
public class PrintWriter {
    /**
     * 控制台输出 - 红色
     */
    public static final String RED = "\033[0;31m";
    /**
     * 控制台输出 - 绿色
     */
    public static final String GREEN = "\033[0;32m";
    /**
     * 控制台输出 - 黄色
     */
    public static final String YELLOW = "\033[0;33m";
    /**
     * 控制台输出 - 蓝色
     */
    public static final String BLUE = "\033[0;34m";
    /**
     * 控制台输出 - 颜色结束并换行
     */
    public static final String RESET = "\033[0m\n";
    /**
     * 控制台输出 - 颜色结束
     */
    public static final String RESET_LINE = "\033[0m";

    /**
     * 打印
     *
     * @param color  颜色
     * @param msg    内容
     * @param isEnd  是否换色
     * @param isLine 是否换行
     */
    public static void print(String color, String msg, boolean isEnd, boolean isLine) {
        if (isLine) {
            System.out.print(color + msg + (isEnd ? RESET : RESET_LINE));
        } else {
            System.out.println(color + msg + (isEnd ? RESET : RESET_LINE));
        }
    }
}
