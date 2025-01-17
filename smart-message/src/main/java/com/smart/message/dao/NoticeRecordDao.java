package com.smart.message.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.message.NoticeRecordEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 通知公告发布记录
 *
 * @author wf
 * @since 2024-12-13 17:46:05
 */
@Mapper
public interface NoticeRecordDao extends BaseDao<NoticeRecordEntity> {

}
