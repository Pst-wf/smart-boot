package com.smart.message.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.message.MessageUserEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 消息-用户表
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@Mapper
public interface MessageUserDao extends BaseDao<MessageUserEntity> {
    /**
     * 查询接收到的消息
     *
     * @param wrapper     查询条件
     * @param messageUser 消息用户bean
     * @return List
     */
    List<MessageUserEntity> findReceive(@Param(Constants.WRAPPER) QueryWrapper<MessageUserEntity> wrapper, @Param("messageUser") MessageUserEntity messageUser);
}
