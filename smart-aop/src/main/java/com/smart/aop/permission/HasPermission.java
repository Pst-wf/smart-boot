package com.smart.aop.permission;

import java.lang.annotation.*;

/**
 * 权限验证注解
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasPermission {
    String[] value() default {};
}
