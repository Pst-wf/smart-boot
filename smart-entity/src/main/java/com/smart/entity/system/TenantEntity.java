package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 租户表
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_tenant")
public class TenantEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String tenantName;
    private String domain;
    private String backgroundUrl;
    private String linkman;
    private String contactNumber;
    private String address;
    private Integer accountNumber;
    private Date expireTime;
}
