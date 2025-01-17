package com.smart.service.message;

import com.smart.entity.message.MessageUserEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 消息-用户表 Service
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
public interface MessageUserService extends BaseService<MessageUserEntity> {
    /**
     * 删除
     *
     * @param messageUserEntity 消息-用户bean
     * @return boolean
     */
    boolean updateMessageDeletedStatus(MessageUserEntity messageUserEntity);

    /**
     * 标记已读
     *
     * @param messageUserEntity 消息-用户bean
     * @return boolean
     */
    boolean setRead(MessageUserEntity messageUserEntity);

    /**
     * 获取未读信息数量
     *
     * @return java.lang.Long
     */
    Integer getMessageCount();
}

