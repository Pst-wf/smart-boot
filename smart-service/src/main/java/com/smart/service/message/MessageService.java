package com.smart.service.message;

import com.github.pagehelper.Page;
import com.smart.entity.message.MessageEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 消息表 Service
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
public interface MessageService extends BaseService<MessageEntity> {
    /**
     * 发送信息
     *
     * @param messageEntity 消息表bean
     */
    void send(MessageEntity messageEntity);

    /**
     * 发送站内信
     *
     * @param messageEntity 消息表bean
     */
    void sendMessage(MessageEntity messageEntity);

    /**
     * 查看
     *
     * @param id 主键ID
     * @return bean
     */
    MessageEntity view(String id);

    /**
     * 删除
     *
     * @param messageEntity 参数0
     */
    void updateDeletedValue(MessageEntity messageEntity);

    /**
     * 回收站列表
     *
     * @param entity 参数0
     * @return com.github.pagehelper.Page<com.smart.entity.message.MessageEntity>
     */
    Page<MessageEntity> collectionPage(MessageEntity entity);
}

