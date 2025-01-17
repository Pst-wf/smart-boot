package com.smart.entity.job;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 任务
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_job")
public class JobEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 执行器主键ID
     */
    @Column(name = "job_group", queryType = QueryType.EQ, isNull = false)
    private String jobGroup;

    /**
     * 任务描述
     */
    @Column(queryType = QueryType.LIKE, isNull = false)
    private String jobDesc;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 调度类型
     */
    @Column(isNull = false)
    private String scheduleType;

    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;

    /**
     * 调度过期策略
     */
    @Column(isNull = false)
    private String misfireStrategy;

    /**
     * 执行器路由策略
     */
    private String executorRouteStrategy;

    /**
     * 执行器任务handler
     */
    @Column(name = "executor_handler", queryType = QueryType.LIKE)
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    private String executorParam;

    /**
     * 阻塞处理策略
     */
    private String executorBlockStrategy;

    /**
     * 任务执行超时时间，单位秒
     */
    @Column(isNull = false)
    private Integer executorTimeout;

    /**
     * 失败重试次数
     */
    @Column(isNull = false)
    private Integer executorFailRetryCount;

    /**
     * GLUE类型
     */
    @Column(isNull = false)
    private String glueType;

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * GLUE备注
     */
    private String glueRemark;

    /**
     * GLUE更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date glueUpdateTime;

    /**
     * 子任务ID，多个逗号分隔
     */
    private String childJobId;

    /**
     * 调度状态：0-停止，1-运行
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer triggerStatus;

    /**
     * 上次调度时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long triggerLastTime;

    /**
     * 下次调度时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long triggerNextTime;

    /**
     * 执行器名称
     */
    @Column(name = "b.title", queryType = QueryType.LIKE)
    @TableField(exist = false)
    private String groupName;

    /**
     * 执行地址
     */
    @JSONField(serialize = false)
    @TableField(exist = false)
    private String addressList;
}
