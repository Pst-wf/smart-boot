package com.smart.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 基础表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class BaseEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 状态（0正常 1删除)
     */
    @TableField(fill = FieldFill.INSERT)
    public String isDeleted;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    public String createBy;
    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    public String createUser;
    /**
     * 创建人部门ID
     */
    @TableField(fill = FieldFill.INSERT)
    public String createDept;
    /**
     * 创建人机构ID
     */
    @TableField(fill = FieldFill.INSERT)
    public String createOrganization;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createDate;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public String updateBy;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date updateDate;
    /**
     * 备注信息
     */
    public String remarks;
    /**
     * 创建人名称
     */
    @TableField(exist = false)
    public String createUserName;
    /**
     * 创建人账号
     */
    @TableField(exist = false)
    public String createUserAccount;
    /**
     * 创建人部门名称
     */
    @TableField(exist = false)
    public String createDeptName;
    /**
     * 创建人机构名称
     */
    @TableField(exist = false)
    public String createOrganizationName;

}
