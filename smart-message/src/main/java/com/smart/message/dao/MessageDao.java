package com.smart.message.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.message.MessageEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 消息表
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@Mapper
public interface MessageDao extends BaseDao<MessageEntity> {
    /**
     * 回收站发件箱列表
     *
     * @param wrapper 参数0
     * @return java.util.List<com.smart.entity.message.MessageEntity>
     */
    List<MessageEntity> findCollectionList(@Param(Constants.WRAPPER) QueryWrapper<MessageEntity> wrapper);
    /**
     * 更新删除标识
     *
     * @param isDeleted 参数0
     * @param ids 参数1
     */
    void updateDeletedValue(@Param("isDeleted")String isDeleted, @Param("ids") List<String> ids);
    /**
     * 详情
     *
     * @param id 参数0
     * @return com.smart.entity.message.MessageEntity
     */
    MessageEntity getOne(@Param("id") String id);
}
