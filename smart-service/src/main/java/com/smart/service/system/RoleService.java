package com.smart.service.system;

import com.smart.entity.system.ButtonsEntity;
import com.smart.entity.system.RoleEntity;
import com.smart.entity.system.ScopeEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;

/**
 * 角色 Service
 *
 * @author wf
 * @since 2022-01-01 17:28:23
 */
public interface RoleService extends BaseService<RoleEntity> {

    /**
     * 保存角色菜单
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    boolean setRoleMenus(RoleEntity roleEntity);

    /**
     * 保存角色按钮
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    boolean setRoleButtons(RoleEntity roleEntity);

    /**
     * 保存角色数据权限
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    boolean setRoleScopes(RoleEntity roleEntity);

    /**
     * 获取角色按钮
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ButtonsEntity> getButtons(String roleId);

    /**
     * 获取角色数据权限
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ScopeEntity> getScopes(String roleId);
}

