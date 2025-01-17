package com.smart.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.UserEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {

    /**
     * 通过角色ID获取数据权限列表
     *
     * @param id 用户ID
     * @return List
     */
    UserEntity getUserWithIdentity(String id);

    /**
     * 用户分页
     *
     * @param wrapper 条件
     * @param entity  用户Bean
     * @return List
     */
    List<UserEntity> findPageWithIdentity(@Param(Constants.WRAPPER) QueryWrapper<UserEntity> wrapper, @Param("user") UserEntity entity);

    /**
     * 通过用户ID获取用户
     *
     * @param id 用户ID
     * @return UserEntity
     */
    UserEntity getUserById(String id);

    /**
     * 启用/停用
     *
     * @param userStatus 账号状态
     * @param date       日期
     * @param id         用户ID
     * @param identityId 身份ID
     * @return boolean
     */
    boolean updateUserStatus(@Param("userStatus") String userStatus, @Param("date") Date date, @Param("id") String id, @Param("identityId") String identityId);

    /**
     * 更新用户登录信息
     *
     * @param lastLoginIp   最后登录IP
     * @param id            用户ID
     * @param lastLoginDate 最后登录时间
     * @return boolean
     */
    boolean updateLoginInfo(@Param("lastLoginIp") String lastLoginIp, @Param("id") String id, @Param("lastLoginDate") Date lastLoginDate);

    /**
     * 获取用户身份信息集合
     *
     * @param userId 用户ID
     * @return List
     */
    List<IdentityEntity> findIdentityList(@Param("userId") String userId);

}
