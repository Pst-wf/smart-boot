package com.smart.common.utils;

import com.smart.common.constant.TokenConstant;
import com.smart.common.vo.UserVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.smart.common.constant.SmartConstant.SYSTEM_ID;
import static com.smart.common.constant.TokenConstant.HEADER;
import static com.smart.common.utils.JwtUtil.parseJwt;

/**
 * Auth工具类
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Slf4j
public class AuthUtil {
    /**
     * 获取当然登陆人
     */
    public static UserVO getUser() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        } else {
            Object user = request.getAttribute("user");
            if (user == null) {
                user = getUser(request);
                if (user != null) {
                    request.setAttribute("user", user);
                }
            }
            return (UserVO) user;
        }
    }

    public static UserVO getUser(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return null;
        } else {
            UserVO user = new UserVO();
            user.setUserId(StringUtil.toStr(claims.get(TokenConstant.USER_ID)));
            user.setNickname(StringUtil.toStr(claims.get(TokenConstant.NICKNAME)));
            user.setUsername(StringUtil.toStr(claims.get(TokenConstant.USERNAME)));
            user.setIsSys(StringUtil.toStr(claims.get(TokenConstant.IS_SYS)));
            user.setIdentityId(StringUtil.toStr(claims.get(TokenConstant.IDENTITY_ID)));
            user.setPostId(StringUtil.toStr(claims.get(TokenConstant.POST_ID)));
            user.setRoleId(StringUtil.toStr(claims.get(TokenConstant.ROLE_ID)));
            user.setDeptId(StringUtil.toStr(claims.get(TokenConstant.DEPT_ID)));
            user.setOrganizationId(StringUtil.toStr(claims.get(TokenConstant.ORGANIZATION_ID)));
            user.setPostName(StringUtil.toStr(claims.get(TokenConstant.POST_NAME)));
            user.setRoleName(StringUtil.toStr(claims.get(TokenConstant.ROLE_NAME)));
            user.setDeptName(StringUtil.toStr(claims.get(TokenConstant.DEPT_NAME)));
            user.setOrganizationName(StringUtil.toStr(claims.get(TokenConstant.ORGANIZATION_NAME)));
            user.setPhone(StringUtil.toStr(claims.get(TokenConstant.PHONE)));
            user.setAvatar(StringUtil.toStr(claims.get(TokenConstant.AVATAR)));
            user.setGender(StringUtil.toStr(claims.get(TokenConstant.GENDER)));
            user.setEmail(StringUtil.toStr(claims.get(TokenConstant.EMAIL)));
            user.setUserType(StringUtil.toStr(claims.get(TokenConstant.USER_TYPE)));
            return user;
        }
    }

    /**
     * 验证是否超级管理员
     */
    public static boolean isSuperAdmin() {
        return StringUtil.notBlankAndEquals(getUserId(), SYSTEM_ID);
    }

    /**
     * 获取身份ID
     */
    public static String getIdentityId() {
        UserVO user = getUser();
        return null == user ? null : user.getIdentityId();
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        UserVO user = getUser();
        return null == user ? null : user.getUserId();
    }

    /**
     * 获取用户账号
     */
    public static String getUsername() {
        UserVO user = getUser();
        return null == user ? null : user.getUsername();
    }

    /**
     * 获取用户名称
     */
    public static String getNickname() {
        UserVO user = getUser();
        return null == user ? null : user.getNickname();
    }

    /**
     * 获取部门ID
     */
    public static String getDeptId() {
        UserVO user = getUser();
        return null == user ? null : user.getDeptId();
    }

    /**
     * 获取角色ID
     */
    public static String getRoleId() {
        UserVO user = getUser();
        return null == user ? null : user.getRoleId();
    }

    /**
     * 获取岗位ID
     */
    public static String getPostId() {
        UserVO user = getUser();
        return null == user ? null : user.getPostId();
    }

    /**
     * 获取机构ID
     */
    public static String getOrganizationId() {
        UserVO user = getUser();
        return null == user ? null : user.getOrganizationId();
    }


    /**
     * 获取部门名称
     */
    public static String getDeptName() {
        UserVO user = getUser();
        return null == user ? null : user.getDeptName();
    }

    /**
     * 获取角色名称
     */
    public static String getRoleName() {
        UserVO user = getUser();
        return null == user ? null : user.getRoleName();
    }

    /**
     * 获取岗位名称
     */
    public static String getPostName() {
        UserVO user = getUser();
        return null == user ? null : user.getPostName();
    }

    /**
     * 获取机构名称
     */
    public static String getOrganizationName() {
        UserVO user = getUser();
        return null == user ? null : user.getOrganizationName();
    }

    /**
     * 获取请求
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取令牌信息
     */
    public static String getToken(HttpServletRequest request) {
        String auth = request.getHeader(HEADER);
        String token;
        if (StringUtil.isNotBlank(auth)) {
            token = JwtUtil.getToken(auth);
        } else {
            token = request.getParameter(HEADER);
        }
        if (StringUtil.isNotBlank(token)) {
            return token;
        }
        return null;
    }

    /**
     * 获取令牌信息
     */
    public static Claims getClaims(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtil.isNotBlank(token)) {
            try {
                return parseJwt(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}