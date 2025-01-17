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
 * 任务注册
 *
 * @author wf
 * @since 2022-04-18 13:05:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_job_registry")
public class JobRegistryEntity extends BaseIdEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 注册组
	 */
    @Column(isNull = false)
    private String registryGroup;

	/**
	 * 注册key
	 */
    @Column(isNull = false)
    private String registryKey;

	/**
	 * 注册值
	 */
    @Column(isNull = false)
    private String registryValue;

	/**
	 * 更新时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date updateTime;

}
