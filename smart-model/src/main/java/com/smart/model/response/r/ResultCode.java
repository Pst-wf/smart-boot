package com.smart.model.response.r;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统错误码枚举
 *
 * @author Chill
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, MessageProperties.SUCCESS),
    /**
     * 操作失败
     */
    FAIL(400, MessageProperties.FAIL),
    /**
     * 业务异常
     */
    FAILURE(400, MessageProperties.FAILURE),
    /**
     * 请求未授权
     */
    UN_AUTH(403, MessageProperties.UN_AUTH),
    /**
     * 登录超时
     */
    OVER_TIME(401, MessageProperties.OVER_TIME),
    /**
     * 未登录
     */
    NOT_LOGIN(401, MessageProperties.NOT_LOGIN),
    /**
     * 该用户已停用
     */
    USER_FROZEN(402, MessageProperties.USER_FROZEN),
    /**
     * 权限不足
     */
    NO_PERMISSION(403, MessageProperties.NO_PERMISSION),

    /**
     * 获取登陆身份信息失败
     */
    UN_CHOOSE_IDENTITY(405, MessageProperties.UN_CHOOSE_IDENTITY),
    /**
     * refresh_token 超时
     */
    REFRESH_TOKEN_OVER_TIME(406, MessageProperties.REFRESH_TOKEN_OVER_TIME),
    /**
     * 租户ID未找到
     */
    TENANT_NOT_FOUND(407, MessageProperties.TENANT_NOT_FOUND),
    /**
     * 用户名或密码错误
     */
    USER_NOT_FOUND(408, MessageProperties.USER_NOT_FOUND),
    /**
     * 未获得用户的租户信息
     */
    USER_HAS_NO_TENANT(409, MessageProperties.USER_HAS_NO_TENANT),
    /**
     * 租户授权已过期,请联系管理员
     */
    USER_HAS_NO_TENANT_PERMISSION(410, MessageProperties.USER_HAS_NO_TENANT_PERMISSION),
    /**
     * 验证码不正确
     */
    CAPTCHA_NOT_CORRECT(411, MessageProperties.CAPTCHA_NOT_CORRECT),
    /**
     * 未获得用户的角色信息
     */
    USER_HAS_NO_ROLE(412, MessageProperties.USER_HAS_NO_ROLE),
    /**
     * 获取用户类型异常
     */
    USER_TYPE_ERROR(413, MessageProperties.USER_TYPE_ERROR),
    /**
     * 服务器异常
     */
    ERROR(500, MessageProperties.ERROR);


    /**
     * code编码
     */
    final int code;
    /**
     * 中文信息描述
     */
    final String message;

}
