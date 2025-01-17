package com.smart.mybatis.annotation;

import com.smart.mybatis.enums.QueryType;

import java.lang.annotation.*;

/**
 * 字段注解
 *
 * @author wf
 * @apiNote 验证及查询条件生成
 * @since 2022-07-26 00:00:00
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    /****** 验证项 ******/
    boolean isNull() default true; //是否允许为null或者'';

    boolean isNumber() default false; //是否是数字;

    double max() default -1; //最大值

    double min() default -1; //最小值

    long length() default 255; //字段长度

    int pointCount() default -1; //小数点后保留几位

    boolean tel() default false; //验证手机号

    boolean idCard() default false; //验证身份证

    boolean email() default false; //验证邮箱

    String lessThan() default ""; //小于等于

    String greaterThan() default ""; //大于等于

    /****** 条件生成 ******/
    QueryType queryType() default QueryType.NONE;

    String separator() default ","; // 分隔符
}
