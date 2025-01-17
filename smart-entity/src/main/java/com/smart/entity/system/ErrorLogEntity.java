package com.smart.entity.system;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

/**
 * 错误日志
 *
 * @author wf
 * @since 2022-07-27 14:27:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_error_log")
public class ErrorLogEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 异常ID
     */
    @Column(name = "error_id", queryType = QueryType.EQ)
    private String errorId;
    /**
     * 异常类型
     */
    private String exceptionClass;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    /**
     * 堆栈信息
     */
    private byte[] stacktrace;

    /**
     * 方法名
     */
    private String logMethod;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 操作人
     */
    @Column(name = "user_nickname", queryType = QueryType.LIKE)
    private String userNickname;

    /**
     * 操作人账号
     */
    @Column(name = "username", queryType = QueryType.LIKE)
    private String username;

    /**
     * IP地址
     */
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @TableField(exist = false)
    private List<StackTraceElement> stacktraceList;


    @Column(name = "create_date", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    @JSONField(serialize = false)
    private List<String> createDateValue;
}
