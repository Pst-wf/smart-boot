package com.smart.entity.job;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 执行器
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_job_group")
public class JobGroupEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 执行器
     */
    @Column(name = "app_name", queryType = QueryType.LIKE, isNull = false)
    private String appName;

    /**
     * 执行器名称
     */
    @Column(name = "title", queryType = QueryType.LIKE, isNull = false)
    private String title;

    /**
     * 执行器类型
     */
    @Column(name = "address_type", queryType = QueryType.EQ, isNull = false)
    private String addressType;

    /**
     * 执行器地址
     */
    private String addressList;

    /**
     * 执行器状态
     */
    @TableField(fill = FieldFill.INSERT)
    private String onlineStatus;

    /**
     * 执行器地址列表(系统注册)
     */
    @TableField(exist = false)
    private List<String> registryList;

    public List<String> getRegistryList() {
        if (addressList != null && !addressList.trim().isEmpty()) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

}
