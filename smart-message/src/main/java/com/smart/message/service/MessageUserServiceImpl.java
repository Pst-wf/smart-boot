package com.smart.message.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.message.MessageUserEntity;
import com.smart.message.dao.MessageDao;
import com.smart.message.dao.MessageUserDao;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.message.MessageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.smart.common.constant.SmartConstant.*;


/**
 * 消息-用户表 ServiceImpl
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@Service("messageUserService")
@Transactional(rollbackFor = Exception.class)
public class MessageUserServiceImpl extends BaseServiceImpl<MessageUserDao, MessageUserEntity> implements MessageUserService {
    @Autowired
    MessageDao messageDao;

    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<MessageUserEntity> findPage(MessageUserEntity entity) {
        String userId = AuthUtil.getUserId();
        if (userId != null && !userId.equals(SYSTEM_ID)) {
            entity.setReceiveUser(userId);
        }
        if (StringUtil.isBlank(entity.getSortField()) && StringUtil.isBlank(entity.getSortOrder())) {
            entity.setSortField(MessageUserEntity.Fields.sendTime);
            entity.setSortOrder("DESC");
        }
        QueryWrapper<MessageUserEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        Page<MessageUserEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        baseMapper.findReceive(wrapper, entity);
        return page;
    }

    /**
     * 详情
     *
     * @param id 主键ID
     * @return bean
     */
    @Override
    public MessageUserEntity get(String id) {
        MessageUserEntity messageUserEntity = super.get(id);
        messageUserEntity.setIsRead(YES);
        messageUserEntity.setReadTime(new Date());
        super.updateById(messageUserEntity);
        messageUserEntity.setMessage(messageDao.selectById(messageUserEntity.getMessageId()));
        return messageUserEntity;
    }

    /**
     * 删除
     *
     * @param messageUserEntity 消息-用户bean
     * @return boolean
     */
    @Override
    public boolean updateMessageDeletedStatus(MessageUserEntity messageUserEntity) {
        if (ListUtil.isEmpty(messageUserEntity.getDeleteIds())) {
            throw new SmartException("请至少选择一条数据");
        }
        LambdaUpdateChainWrapper<MessageUserEntity> wrapper = new LambdaUpdateChainWrapper<>(baseMapper);
        return wrapper.in(MessageUserEntity::getId, messageUserEntity.getDeleteIds())
                .set(MessageUserEntity::getDeletedStatus, messageUserEntity.getDeletedStatus())
                .update();
    }

    /**
     * 标记已读
     *
     * @param messageUserEntity 消息-用户bean
     * @return boolean
     */
    @Override
    public boolean setRead(MessageUserEntity messageUserEntity) {
        if (ListUtil.isEmpty(messageUserEntity.getSelectIds())) {
            throw new SmartException("请至少选择一条数据");
        }
        LambdaUpdateChainWrapper<MessageUserEntity> wrapper = new LambdaUpdateChainWrapper<>(baseMapper);
        return wrapper.in(MessageUserEntity::getId, messageUserEntity.getSelectIds())
                .set(MessageUserEntity::getIsRead, YES)
                .update();
    }

    /**
     * 获取未读信息数量
     *
     * @return java.lang.Long
     */
    @Override
    public Integer getMessageCount() {
        MessageUserEntity entity = new MessageUserEntity();
        entity.setIsRead(NO);
        String userId = AuthUtil.getUserId();
        if (userId != null && !userId.equals(SYSTEM_ID)) {
            entity.setReceiveUser(userId);
        }
        if (StringUtil.isBlank(entity.getSortField()) && StringUtil.isBlank(entity.getSortOrder())) {
            entity.setSortField(MessageUserEntity.Fields.sendTime);
            entity.setSortOrder("DESC");
        }
        QueryWrapper<MessageUserEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        wrapper.eq("deleted_status","0");
        List<MessageUserEntity> receive = baseMapper.findReceive(wrapper, entity);
        return receive.size();
    }
}