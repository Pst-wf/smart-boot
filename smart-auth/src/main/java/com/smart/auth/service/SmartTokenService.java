package com.smart.auth.service;

import com.smart.common.constant.TokenConstant;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.JwtUtil;
import com.smart.entity.system.FrontUserEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Token
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/7/19
 */
@Service
public class SmartTokenService {
    /**
     * 更新token内容
     *
     * @param request  请求
     * @param user     用户
     * @param identity 身份
     * @return String
     */
    public String dynamicallyUpdateToken(HttpServletRequest request, UserEntity user, IdentityEntity identity) {
        Claims claims = JwtUtil.parseJwt(AuthUtil.getToken(request));
        claims.put(TokenConstant.ROLE_ID, identity.getRoleId());
        claims.put(TokenConstant.ROLE_NAME, identity.getRoleName());
        claims.put(TokenConstant.DEPT_ID, identity.getDeptId());
        claims.put(TokenConstant.DEPT_NAME, identity.getDeptName());
        claims.put(TokenConstant.ORGANIZATION_ID, identity.getOrganizationId());
        claims.put(TokenConstant.ORGANIZATION_NAME, identity.getOrganizationName());
        claims.put(TokenConstant.POST_ID, identity.getPostId());
        claims.put(TokenConstant.POST_NAME, identity.getPostName());
        claims.put(TokenConstant.IDENTITY_ID, identity.getId());
        claims.put(TokenConstant.IDENTITY_LIST, user.getIdentityList());
        claims.put("user_name", user.getUsername());
        claims.put(TokenConstant.USER_ID, user.getId());
        claims.put(TokenConstant.USERNAME, user.getUsername());
        claims.put(TokenConstant.NICKNAME, user.getNickname());
        claims.put(TokenConstant.AVATAR, user.getAvatar());
        claims.put(TokenConstant.PHONE, user.getPhone());
        claims.put(TokenConstant.IS_SYS, user.getIsSys());
        return JwtUtil.createJWT(claims.getId(), claims, claims.getExpiration());
    }

    /**
     * 更新app端token内容
     *
     * @param request  请求
     * @param user     用户
     * @return String
     */
    public String dynamicallyUpdateAppToken(HttpServletRequest request, FrontUserEntity user) {
        Claims claims = JwtUtil.parseJwt(AuthUtil.getToken(request));
        claims.put("user_name", user.getUsername());
        claims.put(TokenConstant.USER_ID, user.getId());
        claims.put(TokenConstant.USERNAME, user.getUsername());
        claims.put(TokenConstant.NICKNAME, user.getNickname());
        claims.put(TokenConstant.AVATAR, user.getAvatar());
        claims.put(TokenConstant.PHONE, user.getPhone());
        return JwtUtil.createJWT(claims.getId(), claims, claims.getExpiration());
    }
}
