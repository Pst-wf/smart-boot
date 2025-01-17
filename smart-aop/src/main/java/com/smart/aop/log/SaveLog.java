package com.smart.aop.log;


import java.lang.annotation.*;

/**
 * 生成日志注解
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SaveLog {

    /**
     * 模块
     */
    String module() default "";

    /**
     * 类型
     */
    LogType type();
}
