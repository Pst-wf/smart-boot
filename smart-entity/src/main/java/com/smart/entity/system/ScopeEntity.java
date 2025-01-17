package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 数据权限
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_scope")
public class ScopeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @Column(queryType = QueryType.EQ)
    private String menuId;

    /**
     * 数据权限名称
     */
    @Column(queryType = QueryType.LIKE)
    private String scopeName;

    /**
     * 数据权限字段
     */
    private String scopeField;

    /**
     * 数据权限类名
     */
    private String scopeClass;

    /**
     * 数据权限类型
     */
    private String scopeType;

    /**
     * 自定义数据权限规则
     */
    private String scopeSql;

    /**
     * 可见IDS
     */
    private String visibilityIds;

}
