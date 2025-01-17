package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 角色-菜单
 *
 * @author wf
 * @since 2022-01-01 17:28:23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_role_menu")
public class RoleMenuEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;


}
