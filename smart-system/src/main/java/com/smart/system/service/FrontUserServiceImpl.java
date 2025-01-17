package com.smart.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.utils.*;
import com.smart.entity.system.FrontUserEntity;
import com.smart.entity.system.UserEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.FrontUserService;
import com.smart.system.dao.FrontUserDao;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smart.common.constant.SmartConstant.NO;
import static com.smart.common.constant.SmartConstant.YES;

/**
 * 前台用户 ServiceImpl
 *
 * @author wf
 * @since 2024-08-08 16:20:30
 */
@Service("frontUserService")
@Transactional(rollbackFor = Exception.class)
public class FrontUserServiceImpl extends BaseServiceImpl<FrontUserDao, FrontUserEntity> implements FrontUserService {

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(FrontUserEntity entity, boolean isAdd) {
        if (isAdd) {
            // 新增
            String password = DigestUtil.md5Hex(entity.getPassword());
            String passwordBase = CryptoUtil.encrypt(entity.getPassword());
            entity.setPassword(password);
            entity.setPasswordBase(passwordBase);
        } else {
            //修改
            // 验证账号是否有人使用过
            FrontUserEntity user = super.getById(entity.getId());
            String oldPassword = user.getPassword();
            //判断是否修改密码
            if (StringUtil.isNotBlank(entity.getPassword())) {
                if (!oldPassword.equals(entity.getPassword())) {
                    String password = DigestUtil.md5Hex(entity.getPassword());
                    String passwordBase = CryptoUtil.encrypt(entity.getPassword());
                    entity.setPassword(password);
                    entity.setPasswordBase(passwordBase);
                }
            }
        }
    }

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
    @Override
    public FrontUserEntity createAppUserByWechat(String phoneNumber, String nickName, String gender, String avatarUrl, String openId, String unionid) {
        FrontUserEntity user = new FrontUserEntity();
        user.setUserStatus(YES);
        user.setPassword("123456");
        user.setUsername(phoneNumber);
        user.setGender(gender);
        user.setWxOpenid(openId);
        user.setWxUnionid(unionid);
        user.setPhone(phoneNumber);
        user.setNickname(nickName);
        user.setAvatar(avatarUrl);
        return super.saveEntity(user);
    }

    /**
     * 获取缓存App用户信息
     *
     * @param id 用户ID
     * @return UserEntity
     */
    @Override
    @Cacheable(cacheNames = "app_user", key = "#id", unless = "#result == null")
    public FrontUserEntity getCacheUser(String id) {
        return super.getById(id);
    }

    /**
     * 更新用户状态
     *
     * @param entity app用户
     * @return boolean
     */
    @Override
    @CacheEvict(cacheNames = "app_user", key = "#entity.id")
    public boolean updateUserStatus(FrontUserEntity entity) {
        String identityId = AuthUtil.getIdentityId();
        String userStatus = entity.getUserStatus();
        if (YES.equals(userStatus)) {
            return baseMapper.updateUserStatus(YES, entity.getId(), identityId);
        } else {
            return baseMapper.updateUserStatus(NO, entity.getId(), identityId);
        }
    }

    /**
     * 修改
     *
     * @param entity bean实体
     * @return bean
     */
    @Override
    @CachePut(cacheNames = "app_user", key = "#entity.id", unless = "#result == null")
    public FrontUserEntity updateEntity(FrontUserEntity entity) {
        return super.updateEntity(entity);
    }

    /**
     * 删除
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean delete(FrontUserEntity entity) {
        boolean b = super.delete(entity);
        if (b) {
            // 清除用户缓存
            CacheUtil.evictKeys("app_user", entity.getDeleteIds());
        }
        return b;
    }

    /**
     * 更新登录信息
     *
     * @param user 用户
     */
    @Override
    public void updateLoginInfo(FrontUserEntity user) {
        baseMapper.updateLoginInfo(user.getLastLoginIp(), user.getId(), user.getLastLoginDate());
    }
}