package com.smart.model.response.r;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 消息
 *
 * @author wf
 * @apiNote ignoreUnknownFields 当配置文件中有一个属性实际上没有绑定到 @ConfigurationProperties 类时，
 * 触发告知手动从 application.properties 删除这个属性
 * @since 2022/7/10
 */
@Component
@PropertySource(value = "classpath:message.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "message", ignoreUnknownFields = false)
@Getter
public class MessageProperties {
    public static String SUCCESS;
    public static String FAIL;
    public static String FAILURE;
    public static String ERROR;
    public static String OVER_TIME;
    public static String UN_AUTH;
    public static String USER_FROZEN;
    public static String NOT_LOGIN;
    public static String NO_PERMISSION;
    public static String UN_CHOOSE_IDENTITY;
    public static String REFRESH_TOKEN_OVER_TIME;
    public static String TENANT_NOT_FOUND;
    public static String USER_NOT_FOUND;
    public static String USER_HAS_NO_TENANT;
    public static String USER_HAS_NO_TENANT_PERMISSION;
    public static String CAPTCHA_NOT_CORRECT;
    public static String USER_HAS_NO_ROLE;
    public static String USER_TYPE_ERROR;

    public void setSuccess(String success) {
        MessageProperties.SUCCESS = success;
    }

    public void setFail(String fail) {
        MessageProperties.FAIL = fail;
    }

    public void setFailure(String failure) {
        MessageProperties.FAILURE = failure;
    }

    public void setError(String error) {
        MessageProperties.ERROR = error;
    }

    public void setOverTime(String overTime) {
        OVER_TIME = overTime;
    }

    public void setUnAuth(String unAuth) {
        UN_AUTH = unAuth;
    }

    public void setUserFrozen(String userFrozen) {
        USER_FROZEN = userFrozen;
    }

    public void setNotLogin(String notLogin) {
        NOT_LOGIN = notLogin;
    }

    public void setNoPermission(String noPermission) {
        NO_PERMISSION = noPermission;
    }

    public void setUnChooseIdentity(String unChooseIdentity) {
        UN_CHOOSE_IDENTITY = unChooseIdentity;
    }

    public void setRefreshTokenOverTime(String refreshTokenOverTime) {
        REFRESH_TOKEN_OVER_TIME = refreshTokenOverTime;
    }

    public void setTenantNotFound(String tenantNotFound) {
        TENANT_NOT_FOUND = tenantNotFound;
    }

    public void setUserNotFound(String userNotFound) {
        USER_NOT_FOUND = userNotFound;
    }
    public void setUserHasNoTenant(String userHasNoTenant) {
        USER_HAS_NO_TENANT = userHasNoTenant;
    }
    public void setUserHasNoTenantPermission(String userHasNoTenantPermission) {
        USER_HAS_NO_TENANT_PERMISSION = userHasNoTenantPermission;
    }
    public void setCaptchaNotCorrect(String captchaNotCorrect) {
        CAPTCHA_NOT_CORRECT = captchaNotCorrect;
    }
    public void setUserHasNoRole(String userHasNoRole) {
        USER_HAS_NO_ROLE = userHasNoRole;
    }
    public void setUserTypeError(String userTypeError) {
        USER_TYPE_ERROR = userTypeError;
    }
}
