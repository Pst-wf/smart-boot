package com.smart.entity.system;


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
import java.util.List;

/**
 * 日志
 *
 * @author wf
 * @since 2022-01-30 22:59:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_login_log")
public class LoginLogEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作人ID
     */
    @Column(name = "user_id", queryType = QueryType.EQ)
    private String userId;

    /**
     * 操作人
     */
    @Column(name = "user_nickname", queryType = QueryType.LIKE)
    private String userNickname;

    /**
     * 操作人账号
     */
    @Column(name = "username", queryType = QueryType.LIKE)
    private String username;

    /**
     * 登录来源
     */
    @Column(name = "grant_type", queryType = QueryType.EQ)
    private String grantType;

    /**
     * IP地址
     */
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createDate;

    @Column(name = "create_date", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    @JSONField(serialize = false)
    private List<String> createDateValue;

}
