package com.smart.entity.message;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

/**
 * 消息表
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_message")
public class MessageEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 消息标题
     */
    @Column(name = "message_title", queryType = QueryType.LIKE)
    private String messageTitle;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 是否同步发送邮箱
     */
    private String isToMail;

    /**
     * 邮箱发件人名称
     */
    private String mailSendName;

    /**
     * 发送状态 （1已发送 0草稿）
     */
    @Column(name = "message_status", queryType = QueryType.EQ)
    private String messageStatus;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 消息接收人IDS
     */
    private String receiveUsers;

    /**
     * 消息接收人姓名
     */
    @TableField(exist = false)
    private String receiveUserNames;

    /**
     * 消息接收人IDS
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private List<String> receiveUserIds;

    /**
     * 是否自动填充
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public Boolean auto = true;

}
