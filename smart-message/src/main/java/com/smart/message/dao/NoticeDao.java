package com.smart.message.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.message.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 通知公告
 *
 * @author wf
 * @since 2024-12-13 17:10:35
 */
@Mapper
public interface NoticeDao extends BaseDao<NoticeEntity> {

}
