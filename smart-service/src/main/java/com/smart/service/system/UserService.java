package com.smart.service.system;

import com.alibaba.fastjson.JSONObject;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;

/**
 * 用户表 Service
 *
 * @author wf
 * @since 2021-12-30 21:03:52
 */
public interface UserService extends BaseService<UserEntity> {

    /**
     * 获取用户和身份
     *
     * @param id 用户id
     * @return UserEntity
     */
    UserEntity getUserWithIdentity(String id);

    /**
     * 更新登录IP和最后登录时间
     *
     * @param userEntity 用户信息
     */
    void updateLoginInfo(UserEntity userEntity);

    /**
     * 启用/停用
     *
     * @param userEntity 用户bean
     * @return boolean
     */
    boolean updateUserStatus(UserEntity userEntity);

    /**
     * 更新个人信息
     *
     * @param entity 用户bean
     * @param userId 当前登陆人ID
     * @return boolean
     */
    UserEntity updateInfo(UserEntity entity, String userId);

    /**
     * 更新个人信息
     *
     * @param jsonObject 密码信息
     * @param userId     当前登陆人ID
     * @return boolean
     */
    UserEntity updatePassword(JSONObject jsonObject, String userId);

    /**
     * 获取缓存用户信息
     *
     * @param id 用户ID
     * @return UserEntity
     */
    UserEntity getCacheUser(String id);

    /**
     * 通过IDS获取用户集合
     *
     * @param ids 主键IDS
     * @return List
     */
    List<UserEntity> getUserListByIds(String ids);

    /**
     * 获取用户身份信息集合
     *
     * @param userId 用户ID
     * @return List
     */
    List<IdentityEntity> findIdentityList(String userId);
}

