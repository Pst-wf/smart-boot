package com.smart.model.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数
 */
@Data
public class LoginAuthRequest implements Serializable {

    private String grant_type;

    private String refresh_token;

    private String scope;

    private String tenantId;

    private String token;

    private String username;

    private String password;

    private String captchaCode;

    private String captchaUuid;
    /* *************************** 自定义字段 *************************** */
    private String userType;
    private String wechatCode;
    private String encryptedData;
    private String sessionKey;
    private String iv;
    private String openId;
    private String unionId;
}
