package com.smart.aop.log;

import lombok.Getter;

/**
 * 日志枚举
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Getter
public enum LogType {
    /**
     * 日志类型 - 新增
     */
    ADD("add"),

    /**
     * 日志类型 - 修改
     */
    UPDATE("update"),

    /**
     * 日志类型 - 删除
     */
    DELETE("delete"),

    /**
     * 日志类型 - 彻底删除
     */
    REAL_DELETE("real_delete");

    LogType(String value) {
        this.value = value;
    }

    private final String value;

}