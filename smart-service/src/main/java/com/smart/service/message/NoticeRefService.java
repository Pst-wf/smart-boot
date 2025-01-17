package com.smart.service.message;

import com.github.pagehelper.Page;
import com.smart.entity.message.NoticeRefEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 通知公告-用户 Service
 *
 * @author wf
 * @since 2024-12-13 17:48:13
 */
public interface NoticeRefService extends BaseService<NoticeRefEntity> {
    /**
     * 标记已读
     *
     * @param id 主键
     * @return boolean
     */
    boolean read(String id);

    /**
     * 撤销
     *
     * @param id 主键
     * @return boolean
     */
    boolean cancel(String id);

    /**
     * 查询用户通知公告
     *
     * @param noticeRefEntity 参数0
     * @return com.github.pagehelper.Page<com.smart.entity.message.NoticeRefEntity>
     */
    Page<NoticeRefEntity> pageForUser(NoticeRefEntity noticeRefEntity);
}

