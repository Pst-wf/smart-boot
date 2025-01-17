package com.smart.entity.message;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

/**
 * 通知公告-用户
 *
 * @author wf
 * @since 2024-12-13 17:48:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_notice_ref")
@ExcelFields({
        @ExcelField(title = "通知公告ID", attrName = "noticeId", align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "发送时间", attrName = "sendTime", align = ExcelField.Align.CENTER, sort = 2),
        @ExcelField(title = "用户ID", attrName = "userId", align = ExcelField.Align.CENTER, sort = 3),
        @ExcelField(title = "是否已读（0否 1是）", attrName = "isRead", dictCode = "sys_yes_or_no", align = ExcelField.Align.CENTER, sort = 4),
        @ExcelField(title = "已读时间", attrName = "readTime", align = ExcelField.Align.CENTER, sort = 5),
        @ExcelField(title = "是否撤销（0否 1是）", attrName = "isCancel", dictCode = "sys_yes_or_no", align = ExcelField.Align.CENTER, sort = 6),
        @ExcelField(title = "撤销时间", attrName = "cancelTime", align = ExcelField.Align.CENTER, sort = 7),
        @ExcelField(title = "发布ID", attrName = "releaseId", align = ExcelField.Align.CENTER, sort = 8)
})
public class NoticeRefEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 通知公告ID
     */
    private String noticeId;
    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
    /**
     * 用户ID
     */
    @Column(name = "user_id", queryType = QueryType.EQ)
    private String userId;
    /**
     * 是否已读（0否 1是）
     */
    @Column(name = "is_read", queryType = QueryType.EQ)
    private String isRead;
    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;
    /**
     * 是否撤销（0否 1是）
     */
    @Column(name = "is_cancel", queryType = QueryType.EQ)
    private String isCancel;
    /**
     * 撤销时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    /**
     * 发布ID
     */
    @Column(name = "release_id", queryType = QueryType.EQ)
    private String releaseId;

    /**
     * 姓名
     */
    @Column(name = "nickname", queryType = QueryType.LIKE)
    @TableField(exist = false)
    private String nickname;

    /**
     * 发送时间Between查询集合
     */
    @Column(name = "send_time", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    private List<String> sendTimeBetweenList;

    /**
     * 标题
     */
    @Column(name = "notice_title", queryType = QueryType.LIKE)
    @TableField(exist = false)
    private String noticeTitle;
    /**
     * 内容
     */
    @TableField(exist = false)
    private String noticeContent;
    /**
     * 类型（1普通 2紧急）
     */
    @Column(name = "notice_type", queryType = QueryType.EQ)
    @TableField(exist = false)
    private String noticeType;
    /**
     * 类别（1公告 2通知）
     */
    @Column(name = "notice_category", queryType = QueryType.EQ)
    @TableField(exist = false)
    private String noticeCategory;

    /**
     * 发件人姓名
     */
    @Column(name = "send_username", queryType = QueryType.LIKE)
    @TableField(exist = false)
    private String sendUsername;

}
