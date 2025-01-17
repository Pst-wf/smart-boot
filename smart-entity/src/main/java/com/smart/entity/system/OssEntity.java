package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 对象存储
 *
 * @author wf
 * @since 2022-03-27 12:25:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_oss")
public class OssEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 存储类型
     */
    @Column(name = "oss_type", queryType = QueryType.EQ)
    private String ossType;

    /**
     * 地址
     */
    private String ossHost;

    /**
     * key
     */
    private String accessKey;

    /**
     * secret
     */
    private String accessSecret;

    /**
     * 桶名
     */
    private String bucket;

    /**
     * 是否启用
     */
    @TableField(fill = FieldFill.INSERT)
    private String ossStatus;

    /**
     * 存储目录
     */
    private String ossDir;

    /**
     * 地区
     */
    private String region;

}
