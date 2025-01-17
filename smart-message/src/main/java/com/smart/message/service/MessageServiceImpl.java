package com.smart.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.common.utils.*;
import com.smart.entity.message.MessageEntity;
import com.smart.entity.message.MessageUserEntity;
import com.smart.entity.system.UserEntity;
import com.smart.message.dao.MessageDao;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.message.MessageService;
import com.smart.service.message.MessageUserService;
import com.smart.service.system.ConfigService;
import com.smart.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.smart.common.constant.SmartConstant.*;


/**
 * 消息表 ServiceImpl
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@Service("messageService")
@Transactional(rollbackFor = Exception.class)
public class MessageServiceImpl extends BaseServiceImpl<MessageDao, MessageEntity> implements MessageService {
    @Autowired
    MessageUserService messageUserService;
    @Autowired
    ConfigService configService;
    @Autowired
    UserService userService;
    @Resource
    JavaMailSender mailSender;

    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<MessageEntity> findPage(MessageEntity entity) {
        QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        String userId = AuthUtil.getUserId();
        if (userId != null && !userId.equals(SYSTEM_ID)) {
            wrapper.eq("create_user", userId);
        }
        if (StringUtil.isBlank(entity.getSortField()) && StringUtil.isBlank(entity.getSortOrder())) {
            wrapper.orderByDesc(ListUtil.newArrayList("send_time", "create_date"));
        }
        Page<MessageEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        //根据条件查询
        List<MessageEntity> list = super.list(wrapper);
        List<String> collect = list.stream().map(MessageEntity::getReceiveUsers).filter(StringUtil::isNotBlank).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            List<String> ids = new ArrayList<>();
            collect.forEach(item -> {
                ids.addAll(Arrays.stream(item.split(",")).collect(Collectors.toList()));
            });
            List<UserEntity> users = userService.getListByIdList(new HashSet<>(ids));
            page.getResult().forEach(item -> {
                if (StringUtil.isNotBlank(item.getReceiveUsers())) {
                    List<String> idList = Arrays.stream(item.getReceiveUsers().split(",")).collect(Collectors.toList());
                    item.setReceiveUserNames(users.stream().filter(x -> idList.contains(x.getId())).map(UserEntity::getNickname).collect(Collectors.joining("、")));
                }
            });
        }
        return page;
    }

    /**
     * 回收站列表
     *
     * @param entity 参数0
     * @return com.github.pagehelper.Page<com.smart.entity.message.MessageEntity>
     */
    @Override
    public Page<MessageEntity> collectionPage(MessageEntity entity) {
        QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper = initWrapperAfter(entity, wrapper);
        String userId = AuthUtil.getUserId();
        if (userId != null && !userId.equals(SYSTEM_ID)) {
            wrapper.eq("create_user", userId);
        }
        if (StringUtil.isBlank(entity.getSortField()) && StringUtil.isBlank(entity.getSortOrder())) {
            wrapper.orderByDesc(ListUtil.newArrayList("send_time", "create_date"));
        }
        wrapper.eq("is_deleted", "2");
        Page<MessageEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        //根据条件查询
        List<MessageEntity> list = baseMapper.findCollectionList(wrapper);
        List<String> collect = list.stream().map(MessageEntity::getReceiveUsers).filter(StringUtil::isNotBlank).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            List<String> ids = new ArrayList<>();
            collect.forEach(item -> {
                ids.addAll(Arrays.stream(item.split(",")).collect(Collectors.toList()));
            });
            List<UserEntity> users = userService.getListByIdList(new HashSet<>(ids));
            page.getResult().forEach(item -> {
                if (StringUtil.isNotBlank(item.getReceiveUsers())) {
                    List<String> idList = Arrays.stream(item.getReceiveUsers().split(",")).collect(Collectors.toList());
                    item.setReceiveUserNames(users.stream().filter(x -> idList.contains(x.getId())).map(UserEntity::getNickname).collect(Collectors.joining("、")));
                }
            });
        }
        return page;
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(MessageEntity entity, boolean isAdd) {
        if (ListUtil.isNotEmpty(entity.getReceiveUserIds())) {
            entity.setReceiveUsers(StringUtil.join(entity.getReceiveUserIds(), ","));
        }
    }

    /**
     * 发送信息
     *
     * @param messageEntity 消息表bean
     */
    @Override
    public void send(MessageEntity messageEntity) {
        List<String> receiveUserIds = messageEntity.getReceiveUserIds();
        if (ListUtil.isEmpty(receiveUserIds)) {
            throw new SmartException("至少选择一个接收人！");
        }
        if (StringUtil.isBlank(messageEntity.getId())) {
            throw new SmartException("未选择要发送的消息！");
        }
        Date sendTime = new Date();
        List<MessageUserEntity> list = new ArrayList<>();
        ListUtil.forEach(receiveUserIds, (index, item) -> {
            MessageUserEntity messageUser = new MessageUserEntity();
            messageUser.setChatId(TimeUtil.createNo(sendTime, 6, index + 1));
            messageUser.setMessageId(messageEntity.getId());
            messageUser.setReceiveUser(item);
            messageUser.setSendTime(sendTime);
            list.add(messageUser);
        });
        if (ListUtil.isNotEmpty(list)) {
            boolean b = messageUserService.saveBatch(list);
            if (!b) {
                throw new SmartException("发送失败！！");
            }
            MessageEntity message = new MessageEntity();
            message.setId(messageEntity.getId());
            message.setMessageStatus(YES);
            boolean updateStatus = super.updateById(message);
            if (!updateStatus) {
                throw new SmartException("更新发送状态失败！");
            }
        }
    }

    /**
     * 发送站内信
     *
     * @param messageEntity 消息表bean
     */
    @Override
    public void sendMessage(MessageEntity messageEntity) {
        if (StringUtil.isNotBlank(messageEntity.getId())) {
            MessageEntity one = super.getById(messageEntity.getId());
            if (one == null) {
                throw new SmartException("未找到该消息！");
            }
            if (one.getMessageStatus().equals(YES)) {
                throw new SmartException("该消息已发送，不可重复发送！");
            }
        }
        Date sendTime = new Date();
        List<String> receiveUserIds = messageEntity.getReceiveUserIds();
        if (ListUtil.isEmpty(receiveUserIds)) {
            throw new SmartException("至少选择一个接收人！");
        }
        messageEntity.setMessageStatus(YES);
        messageEntity.setSendTime(sendTime);
        MessageEntity entity;
        if (StringUtil.isBlank(messageEntity.getId())) {
            entity = super.saveEntity(messageEntity);
        } else {
            entity = super.updateEntity(messageEntity);
        }
        if (entity == null) {
            throw new SmartException("保存消息失败！");
        }
        List<MessageUserEntity> list = new ArrayList<>();
        ListUtil.forEach(receiveUserIds, (index, item) -> {
            MessageUserEntity messageUser = new MessageUserEntity();
            messageUser.setChatId(TimeUtil.createNo(sendTime, 6, index + 1));
            messageUser.setMessageId(entity.getId());
            messageUser.setReceiveUser(item);
            messageUser.setSendTime(sendTime);
            list.add(messageUser);
        });
        if (ListUtil.isNotEmpty(list)) {
            boolean b = messageUserService.saveBatch(list);
            if (!b) {
                throw new SmartException("发送失败！！");
            }
        }
        // 判断是否同步发送邮箱
        if (StringUtil.notBlankAndEquals(entity.getIsToMail(), YES)) {
            String emailConfig = configService.getConfig("sys_email_config");
            if (StringUtil.isBlank(emailConfig)) {
                log.error("获取邮箱配置信息失败，使用默认邮箱配置");
                emailConfig = IOUtil.getValue("json/email.json");
            }
            JSONObject jsonObject = JSON.parseObject(emailConfig);

            List<UserEntity> receiveUsers = userService.getListByIdList(receiveUserIds);
            Set<String> receiveUserMails = receiveUsers.stream().map(UserEntity::getEmail).filter(StringUtil::isNotBlank).collect(Collectors.toSet());
            if (!receiveUserMails.isEmpty()) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper;
                try {
                    String personal = entity.getMailSendName();
                    if (StringUtil.isBlank(personal)) {
                        UserEntity userEntity = userService.get(AuthUtil.getUserId());
                        personal = userEntity.getNickname();
                    }
                    helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setFrom(jsonObject.getString("from"), personal);
                    helper.setTo(receiveUserMails.toArray(new String[0]));
                    helper.setSubject(entity.getMessageTitle());
                    helper.setText(entity.getMessageContent(), true);
                    mailSender.send(mimeMessage);
                } catch (MessagingException | UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 详情
     *
     * @param id 主键ID
     * @return bean
     */
    @Override
    public MessageEntity get(String id) {
        MessageEntity messageEntity = baseMapper.getOne(id);
        if (messageEntity == null) {
            throw new SmartException("未找到该消息！");
        }
        List<UserEntity> users = userService.getUserListByIds(messageEntity.getReceiveUsers());
        messageEntity.setReceiveUserNames(users.stream().map(UserEntity::getNickname).collect(Collectors.joining("、")));
        return messageEntity;
    }


    /**
     * 查看
     *
     * @param id 主键ID
     * @return bean
     */
    @Override
    public MessageEntity view(String id) {
        MessageUserEntity ref = messageUserService.get(id);
        if (ref == null) {
            throw new SmartException("未找到该消息！");
        }
        MessageEntity messageEntity = get(ref.getMessageId());
        if (messageEntity == null) {
            throw new SmartException("未找到该消息！");
        }
        if (StringUtil.notBlankAndEquals(ref.getIsRead(), NO)) {
            MessageUserEntity messageUserEntity = new MessageUserEntity();
            messageUserEntity.setSelectIds(ListUtil.newArrayList(ref.getId()));
            messageUserService.setRead(messageUserEntity);
        }
        return messageEntity;
    }

    /**
     * 删除
     *
     * @param messageEntity 参数0
     */
    @Override
    public void updateDeletedValue(MessageEntity messageEntity) {
        if (ListUtil.isEmpty(messageEntity.getDeleteIds())) {
            throw new SmartException("至少选择一条数据！");
        }
        baseMapper.updateDeletedValue(messageEntity.getIsDeleted(), messageEntity.getDeleteIds());
    }
}