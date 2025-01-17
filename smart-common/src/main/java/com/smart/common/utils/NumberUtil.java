package com.smart.common.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字工具类
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/8/17
 */
public class NumberUtil {
    /**
     * 是否数字正则
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    /**
     * 获取最大值
     *
     * @param b 数字数组
     * @return String
     */
    public static BigDecimal getMax(BigDecimal... b) {
        return Collections.max(Arrays.asList(b));
    }

    /**
     * 获取最小值
     *
     * @param b 数字数组
     * @return String
     */
    public static BigDecimal getMin(BigDecimal... b) {
        return Collections.min(Arrays.asList(b));
    }

    /**
     * 是否是数字
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        if (StringUtil.isBlank(str)) {
            return false;
        } else {
            Matcher isNum = NUMBER_PATTERN.matcher(str);
            return isNum.matches();
        }
    }

    /**
     * 是否越界
     *
     * @param target 目标数据
     * @param min    最小值
     * @param max    最大值
     * @return boolean
     */
    public static boolean isOverStep(BigDecimal target, BigDecimal min, BigDecimal max) {
        if (target != null) {
            if (min != null && max != null) {
                return target.compareTo(min) < 0 || target.compareTo(max) > 0;
            } else if (min != null) {
                return target.compareTo(min) < 0;
            } else if (max != null) {
                return target.compareTo(max) > 0;
            }
        }
        return false;
    }

    /**
     * 是否越界
     *
     * @param target 目标数据
     * @param min    最小值
     * @param max    最大值
     * @return boolean
     */
    public static boolean isOverStep(String target, String min, String max) {
        if (isNumeric(target)) {
            boolean minStatus = isNumeric(min);
            boolean maxStatus = isNumeric(max);
            if (minStatus && maxStatus) {
                return isOverStep(new BigDecimal(target), new BigDecimal(min), new BigDecimal(max));
            } else if (minStatus) {
                return isOverStep(new BigDecimal(target), new BigDecimal(min), null);
            } else if (maxStatus) {
                return isOverStep(new BigDecimal(target), null, new BigDecimal(max));
            }
        }
        return false;
    }
}
