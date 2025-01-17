package com.smart.message.service;

import com.smart.entity.message.NoticeEntity;
import com.smart.message.dao.NoticeDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.message.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知公告 ServiceImpl
 *
 * @author wf
 * @since 2024-12-13 17:10:35
 */
@Service("noticeService")
@Transactional(rollbackFor = Exception.class)
public class NoticeServiceImpl extends BaseServiceImpl<NoticeDao, NoticeEntity> implements NoticeService {

}