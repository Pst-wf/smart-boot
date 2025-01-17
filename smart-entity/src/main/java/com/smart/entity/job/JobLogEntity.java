package com.smart.entity.job;


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

/**
 * 任务执行日志
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_job_log")
public class JobLogEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 执行器主键ID
     */
    @Column(isNull = false)
    private String jobGroup;

    /**
     * 任务，主键ID
     */
    @Column(name = "a.job_id", queryType = QueryType.EQ, isNull = false)
    private String jobId;

    /**
     * 执行器地址，本次执行的地址
     */
    private String executorAddress;

    /**
     * 执行器任务handler
     */
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    private String executorParam;

    /**
     * 执行器任务分片参数，格式如 1/2
     */
    private String executorShardingParam;

    /**
     * 失败重试次数
     */
    @Column(isNull = false)
    private Integer executorFailRetryCount;

    /**
     * 调度-时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date triggerTime;

    /**
     * 调度-结果
     */
    private Integer triggerCode;

    /**
     * 调度-日志
     */
    private String triggerMsg;

    /**
     * 执行-时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handleTime;

    /**
     * 执行-状态
     */
    private Integer handleCode;

    /**
     * 执行-日志
     */
    private String handleMsg;

    /**
     * 告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败
     */
    @Column(isNull = false)
	@TableField(fill = FieldFill.INSERT)
    private Integer alarmStatus;

    /**
     * 执行器名称
     */
    @TableField(exist = false)
    @Column(name = "b.title", queryType = QueryType.LIKE)
    private String groupName;

    /**
     * 任务描述
     */
    @TableField(exist = false)
    @Column(name = "c.job_desc", queryType = QueryType.LIKE)
    private String jobName;

}
