package com.smart.common.constant;

import com.smart.common.utils.JwtUtil;

public class TokenConstant {
    public static final String SIGN_KEY = JwtUtil.SIGN_KEY;
    public static final String AVATAR = "avatar";
    public static final String HEADER = "Authorization";
    public static final String HEADER_PREFIX = "Basic ";
    public static final String BEARER = "bearer";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
    public static final String EXPIRES_IN = "expiresIn";
    public static final String EXPIRES_TIME = "expiresTime";
    public static final String SCOPE = "scope";
    public static final String JTI = "jti";
    public static final String ACCOUNT = "account";
    public static final String IS_SYS = "isSys";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NICKNAME = "nickname";
    public static final String GENDER = "gender";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String REAL_NAME = "realName";
    public static final String IDENTITY_ID = "identityId";
    public static final String USER_ID = "userId";
    public static final String DEPT_ID = "deptId";
    public static final String DEPT_NAME = "deptName";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String ORGANIZATION_NAME = "organizationName";
    public static final String POST_ID = "postId";
    public static final String POST_NAME = "postName";
    public static final String ROLE_ID = "roleId";
    public static final String ROLE_NAME = "roleName";
    public static final String IDENTITY_LIST = "identityList";
    public static final String REMARKS = "remarks";
    public static final String USER_TYPE = "userType";
    public static final String TENANT_ID = "tenantId";
    public static final String CLIENT_ID = "clientId";
    public static final String LICENSE = "license";
    public static final String LICENSE_NAME = "powered by smart";
    public static final String DEFAULT_AVATAR = "";
    public static final Integer AUTH_LENGTH = JwtUtil.AUTH_LENGTH;
    public static final String LAST_LOGIN_DATE = "lastLoginDate";
    public static final String LAST_LOGIN_IP = "lastLoginIp";
}
