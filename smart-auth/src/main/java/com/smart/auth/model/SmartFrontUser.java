package com.smart.auth.model;


import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 当前登录前台用户
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Getter
@FieldNameConstants
public class SmartFrontUser extends User {
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
     * 头像
     */
    private final String avatar;
    /**
     * 租户ID
     */
    private final String tenantId;

    public SmartFrontUser(String username,
                          String password,
                          String userId,
                          String gender,
                          String phone,
                          String avatar,
                          String tenantId,
                          String nickname,
                          boolean enabled,
                          boolean accountNonExpired,
                          boolean credentialsNonExpired,
                          boolean accountNonLocked,
                          Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.gender = gender;
        this.phone = phone;
        this.avatar = avatar;
        this.tenantId = tenantId;
        this.nickname = nickname;
        this.username = username;
    }
}
