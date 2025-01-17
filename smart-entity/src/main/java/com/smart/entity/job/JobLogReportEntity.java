package com.smart.entity.job;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 调度日志
 *
 * @author wf
 * @since 2022-04-18 13:21:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_job_log_report")
public class JobLogReportEntity extends BaseIdEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 调度-时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date triggerDay;

	/**
	 * 运行中-日志数量
	 */
    @Column(isNull = false)
    private Integer runningCount;

	/**
	 * 执行成功-日志数量
	 */
    @Column(isNull = false)
    private Integer sucCount;

	/**
	 * 执行失败-日志数量
	 */
    @Column(isNull = false)
    private Integer failCount;

	/**
	 * 更新时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date updateTime;

}
