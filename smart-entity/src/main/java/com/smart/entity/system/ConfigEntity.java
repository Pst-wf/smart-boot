package com.smart.entity.system;


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
 * 系统配置
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_config")
public class ConfigEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 键
     */
    @Column(name = "config_key", queryType = QueryType.LIKE, isNull = false)
    private String configKey;

    /**
     * 值
     */
    private String configValue;

    /**
     * 描述
     */
    private String configDesc;

    /**
     * 是否系统
     */
    @Column(name = "is_system", queryType = QueryType.EQ, isNull = false)
    private String isSystem;

}
