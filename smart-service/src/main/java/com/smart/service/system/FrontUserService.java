package com.smart.service.system;

import com.smart.entity.system.FrontUserEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 前台用户 Service
 *
 * @author wf
 * @since 2024-08-08 16:20:30
 */
public interface FrontUserService extends BaseService<FrontUserEntity> {

    /**
     * 自主注册（微信）
     *
     * @param phoneNumber 手机号
     * @param nickName    昵称
     * @param gender      性别
     * @param avatarUrl   头像
     * @param openId      微信OpenId
     * @param unionid     微信Unionid
     * @return FrontUserEntity
     */
    FrontUserEntity createAppUserByWechat(String phoneNumber, String nickName, String gender, String avatarUrl, String openId, String unionid);

    /**
     * 获取缓存用户信息
     *
     * @param id 用户ID
     * @return UserEntity
     */
    FrontUserEntity getCacheUser(String id);

    /**
     * 更新用户状态
     *
     * @param frontUserEntity app用户
     * @return boolean
     */
    boolean updateUserStatus(FrontUserEntity frontUserEntity);

    /**
     * 更新登录信息
     *
     * @param user 用户
     */
    void updateLoginInfo(FrontUserEntity user);
}

