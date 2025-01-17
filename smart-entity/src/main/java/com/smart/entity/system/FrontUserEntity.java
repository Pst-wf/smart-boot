package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 前台用户
 *
 * @author wf
 * @since 2024-08-08 16:20:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_front_user")
@ExcelFields({
        @ExcelField(title = "创建人", attrName = "createUserName", align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "创建时间", attrName = "createDate", align = ExcelField.Align.CENTER, sort = 2),
        @ExcelField(title = "用户昵称", attrName = "nickname", align = ExcelField.Align.CENTER, sort = 3),
        @ExcelField(title = "联系电话", attrName = "phone", align = ExcelField.Align.CENTER, sort = 4),
        @ExcelField(title = "性别", attrName = "gender", align = ExcelField.Align.CENTER, sort = 5),
        @ExcelField(title = "头像路径", attrName = "avatar", align = ExcelField.Align.CENTER, sort = 7),
        @ExcelField(title = "绑定的微信号", attrName = "openid", align = ExcelField.Align.CENTER, sort = 8),
        @ExcelField(title = "紧急联系人", attrName = "contactName", align = ExcelField.Align.CENTER, sort = 9),
        @ExcelField(title = "关系", attrName = "relationship", align = ExcelField.Align.CENTER, sort = 10),
        @ExcelField(title = "紧急联系电话", attrName = "contactPhone", align = ExcelField.Align.CENTER, sort = 11),
        @ExcelField(title = "紧急联系地址", attrName = "contactAddress", align = ExcelField.Align.CENTER, sort = 12)
})
public class FrontUserEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 启用状态
     */
    @Column(name = "user_status", queryType = QueryType.EQ)
    private String userStatus;
    /**
     * 用户昵称
     */
    @Column(name = "nickname", queryType = QueryType.LIKE, isNull = false)
    private String nickname;
    /**
     * 登录账号
     */
    @Column(queryType = QueryType.LIKE)
    private String username;
    /**
     * 绑定的微信号
     */
    private String wxOpenid;
    /**
     * 开放平台的唯一标识符
     */
    private String wxUnionid;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 密码加密
     */
    private String passwordBase;
    /**
     * 联系电话
     */
    @Column(name = "phone", queryType = QueryType.LIKE)
    private String phone;
    /**
     * 性别
     */
    private String gender;
    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 最后登陆IP
     */
    private String lastLoginIp;

    /**
     * 最后登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginDate;

}
