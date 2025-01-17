package com.smart.message.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.message.NoticeRefEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 通知公告-用户
 *
 * @author wf
 * @since 2024-12-13 17:48:13
 */
@Mapper
public interface NoticeRefDao extends BaseDao<NoticeRefEntity> {
    /**
     * 查询通知公告用户
     *
     * @param wrapper 查询条件
     * @return java.util.List<com.smart.entity.message.NoticeRefEntity>
     */
    List<NoticeRefEntity> findList(@Param(Constants.WRAPPER) QueryWrapper<NoticeRefEntity> wrapper);

    /**
     * 查询用户通知公告
     *
     * @param wrapper 参数0
     * @return java.util.List<com.smart.entity.message.NoticeRefEntity>
     */
    List<NoticeRefEntity> pageForUser(@Param(Constants.WRAPPER)QueryWrapper<NoticeRefEntity> wrapper);
}
