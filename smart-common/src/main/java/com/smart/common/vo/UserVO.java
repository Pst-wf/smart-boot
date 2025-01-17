package com.smart.common.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 当前登录用户
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录账号
     */
    private String username;
    /**
     * 性别
     */
    private String gender;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否系统 1是 0否
     */
    private String isSys;

    /**
     * 身份ID
     */
    private String identityId;

    /**
     * 岗位ID
     */
    private String postId;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 机构ID
     */
    private String organizationId;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 机构名称
     */
    private String organizationName;

    /**
     * 用户类型
     */
    private String userType;
}
