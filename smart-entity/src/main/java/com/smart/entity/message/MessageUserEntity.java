package com.smart.entity.message;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 消息-用户表
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_message_user")
public class MessageUserEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String chatId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送时间
     */
    @Column(isNull = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 接收人ID
     */
    @Column(name = "receive_user", queryType = QueryType.EQ)
    private String receiveUser;

    /**
     * 是否已读
     */
    @Column(name = "is_read", queryType = QueryType.EQ)
    @TableField(fill = FieldFill.INSERT)
    private String isRead;

    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    /**
     * 删除状态
     */
    @Column(name = "deleted_status", queryType = QueryType.EQ)
    @TableField(fill = FieldFill.INSERT)
    private String deletedStatus;

    /**
     * 消息实体
     */
    @TableField(exist = false)
    private MessageEntity message;

    @Column(name = "send_time", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String sendTimeStr;

    /**
     * 发送人姓名
     */
    @TableField(exist = false)
    private String sendUserName;

    /**
     * 消息标题
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String messageTitle;
}
