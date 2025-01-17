package com.smart.common.annotation;

import java.lang.annotation.*;

/**
 * Response忽略序列化
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/7/29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface ExcludeKeys {
    String[] value() default {};
}
