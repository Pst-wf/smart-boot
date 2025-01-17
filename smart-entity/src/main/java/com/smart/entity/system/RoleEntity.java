package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 角色
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_role")
public class RoleEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    @Column(name = "role_code", queryType = QueryType.LIKE, isNull = false)
    private String roleCode;

    /**
     * 角色名称
     */
    @Column(name = "role_name", queryType = QueryType.LIKE, isNull = false)
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 启用状态
     */
    @Column(name = "status", queryType = QueryType.EQ, isNull = false)
    private String status;

    /**
     * 菜单集合Ids
     */
    @TableField(exist = false)
    private List<String> menuIds;

    /**
     * 菜单集合
     */
    @TableField(exist = false)
    private List<MenuEntity> menus;

    /**
     * 按钮集合Ids
     */
    @TableField(exist = false)
    private List<String> buttonIds;

    /**
     * 按钮集合
     */
    @TableField(exist = false)
    private List<ButtonsEntity> buttons;

    /**
     * 数据权限IDS
     */
    @TableField(exist = false)
    private List<String> scopeIds;
}
