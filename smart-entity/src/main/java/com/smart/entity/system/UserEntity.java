package com.smart.entity.system;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

/**
 * 用户表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_user")
@ExcelFields({
        @ExcelField(title = "昵称", attrName = "nickname", align = ExcelField.Align.CENTER, sort = 10),
        @ExcelField(title = "账号", attrName = "username", align = ExcelField.Align.CENTER, sort = 20),
        @ExcelField(title = "联系电话", attrName = "phone", align = ExcelField.Align.CENTER, sort = 30),
        @ExcelField(title = "机构/部门/角色/岗位", attrName = "identityInfo", align = ExcelField.Align.CENTER, words = 80, sort = 40,type= ExcelField.Type.EXPORT),
})
public class UserEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    @Column(queryType = QueryType.LIKE)
    private String nickname;

    /**
     * 登录账号
     */
    @Column(queryType = QueryType.LIKE)
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 密码加密
     */
    private String passwordBase;

    /**
     * 办公电话
     */
    private String phone;
    /**
     * 性别
     */
    private String gender;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 绑定的微信号
     */
    private String wxOpenid;

    /**
     * 开放平台的唯一标识符
     */
    private String wxUnionid;

    /**
     * GitLab访问令牌
     */
    private String gitLabToken;

    /**
     * 最后登陆IP
     */
    private String lastLoginIp;

    /**
     * 最后登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginDate;

    /**
     * 状态 0 停用 1 正常
     */
    @TableField(fill = FieldFill.INSERT)
    @Column(queryType = QueryType.EQ)
    private String userStatus;

    /**
     * 冻结时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date freezeDate;

    /**
     * 冻结原因
     */
    private String freezeCause;

    /**
     * 是否系统 1是 0否
     */
    @TableField(fill = FieldFill.INSERT)
    private String isSys;

    /**
     * 用户登录token
     */
    @TableField(exist = false)
    private String token;

    /**
     * 身份集合
     */
    @TableField(exist = false)
    private List<IdentityEntity> identityList;

    /**
     * 角色
     */
    @TableField(exist = false)
    private String role;
    /**
     * 选择的identityId
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String identityId;

    /**
     * 用户所属部门/机构/角色
     */
    @TableField(exist = false)
    private String identityInfo;

    /**
     * 拥有的角色
     */
    @TableField(exist = false)
    private String roleNames;

    /**
     * 选择的部门或者公司ID
     */
    @TableField(exist = false)
    private String hasPostId;

    /**
     * 选择的部门或者公司ID
     */
    @TableField(exist = false)
    private String hasDeptIds;

    /**
     * 选择的部门或者公司ID
     */
    @TableField(exist = false)
    private String hasOrganizationIds;

    /**
     * 选择的部门或者公司ID
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String deptId;

    /**
     * 选择的岗位
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String postId;

    /**
     * 选择的角色
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String roleId;

    /**
     * 登录方式
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String loginType;

    /**
     * 单点Code
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String code;

}
