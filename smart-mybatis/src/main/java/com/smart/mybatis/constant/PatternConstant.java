package com.smart.mybatis.constant;

import java.util.regex.Pattern;

/**
 * 正则常量
 *
 * @author wf
 * @version 1.0.0
 * @since 2022-07-26 00:00:00
 */
public class PatternConstant {

    /**
     * 数字验证
     */
    public final static Pattern IS_NAN_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    /**
     * 邮箱验证
     */
    public final static Pattern IS_MAIL_PATTERN = Pattern.compile("^([a-z\\dA-Z]+[-|.]?)+[a-z\\dA-Z]@([a-z\\dA-Z]+(-[a-z\\dA-Z]+)?\\.)+[a-zA-Z]{2,}$");
}
