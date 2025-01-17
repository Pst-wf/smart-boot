package com.smart.common.constant;

/**
 * 鉴权常量
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/8/12
 */
public class AuthConstant {
    /**
     * 密码加密规则
     * 2021-1-22 修改登录时密码加密规则
     * 现在后台不对密码做任何加密处理
     */
    public final static String ENCRYPT = "{noop}";
    /**
     * sys_client表字段
     */
    public final static String CLIENT_FIELDS = "client_id, CONCAT('" + ENCRYPT + "',client_secret) as client_secret, resource_ids, scope, authorized_grant_types, " +
            "web_server_redirect_uri, authorities, access_token_validity, " +
            "refresh_token_validity, additional_information, autoapprove";
    /**
     * sys_client查询语句
     */
    public final static String BASE_STATEMENT = "select " + CLIENT_FIELDS + " from sys_client";
    /**
     * sys_client查询排序
     */
    public final static String DEFAULT_FIND_STATEMENT = BASE_STATEMENT + " order by client_id";
    /**
     * 查询client_id
     */
    public final static String DEFAULT_SELECT_STATEMENT = BASE_STATEMENT + " where client_id = ?";

    public final static String CAPTCHA_HEADER_KEY = "Captcha-Key";
    public final static String CAPTCHA_HEADER_CODE = "Captcha-Code";
    public final static String TENANT_HEADER_KEY = "Tenant-Id";
    public final static String TENANT_PARAM_KEY = "tenantId";
    public final static String IDENTITY_ID = "Identity-Id";
    public final static String DEFAULT_TENANT_ID = "000000";
    public final static String USER_TYPE_HEADER_KEY = "User-Type";
    public final static String DEFAULT_USER_TYPE = "web";
    public final static String USER_TYPE_PC = "pc";
    public final static String USER_TYPE_APP = "app";

    public final static String HEADER_PREFIX = "Basic ";
}
