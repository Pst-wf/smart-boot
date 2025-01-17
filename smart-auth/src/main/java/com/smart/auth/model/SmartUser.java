package com.smart.auth.model;


import com.smart.entity.system.IdentityEntity;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 当前登录用户
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Getter
@FieldNameConstants
public class SmartUser extends User {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private final String userId;

    /**
     * 用户昵称
     */
    private final String nickname;

    /**
     * 登录账号
     */
    private final String username;
    /**
     * 性别
     */
    private final String gender;
    /**
     * 手机号
     */
    private final String phone;
    /**
     * 邮箱
     */
    private final String email;
    /**
     * 头像
     */
    private final String avatar;

    /**
     * 是否系统 1是 0否
     */
    private final Boolean isSys;

    /**
     * 身份ID
     */
    private final String identityId;

    /**
     * 岗位ID
     */
    private final String postId;

    /**
     * 岗位名称
     */
    private final String postName;

    /**
     * 角色ID
     */
    private final String roleId;


    /**
     * 角色名称
     */
    private final String roleName;


    /**
     * 部门ID
     */
    private final String deptId;

    /**
     * 部门名称
     */
    private final String deptName;


    /**
     * 机构ID
     */
    private final String organizationId;

    /**
     * 机构名称
     */
    private final String organizationName;

    /**
     * 租户ID
     */
    private final String tenantId;

    /**
     * 备注
     */
    private final String remarks;

    /**
     * 身份集合
     */
    private final List<IdentityEntity> identityList;

    public SmartUser(String username,
                     String password,
                     String userId,
                     String gender,
                     String phone,
                     String email,
                     String avatar,
                     boolean isSys,
                     String tenantId,
                     String nickname,
                     String identityId,
                     String deptId,
                     String deptName,
                     String postId,
                     String postName,
                     String roleId,
                     String roleName,
                     String organizationId,
                     String organizationName,
                     List<IdentityEntity> identityList,
                     String remarks,
                     boolean enabled,
                     boolean accountNonExpired,
                     boolean credentialsNonExpired,
                     boolean accountNonLocked,
                     Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.identityId = identityId;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.isSys = isSys;
        this.tenantId = tenantId;
        this.nickname = nickname;
        this.username = username;
        this.deptId = deptId;
        this.deptName = deptName;
        this.postId = postId;
        this.postName = postName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.identityList = identityList;
        this.remarks = remarks;
    }
}
