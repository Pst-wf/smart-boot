package com.smart.system.dao;

import com.smart.entity.system.FrontUserEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;


/**
 * 前台用户
 *
 * @author wf
 * @since 2024-08-08 16:20:30
 */
@Mapper
public interface FrontUserDao extends BaseDao<FrontUserEntity> {
    /**
     * 启用/停用
     *
     * @param userStatus 账号状态
     * @param id         用户ID
     * @param identityId 身份ID
     * @return boolean
     */
    boolean updateUserStatus(@Param("userStatus") String userStatus, @Param("id") String id, @Param("identityId") String identityId);

    /**
     * 更新用户登录信息
     *
     * @param lastLoginIp   最后登录IP
     * @param id            用户ID
     * @param lastLoginDate 最后登录时间
     * @return boolean
     */
    boolean updateLoginInfo(@Param("lastLoginIp") String lastLoginIp, @Param("id") String id, @Param("lastLoginDate") Date lastLoginDate);
}
