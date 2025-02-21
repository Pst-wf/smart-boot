package com.smart.service.system;


import com.smart.entity.system.MenuEntity;
import com.smart.model.route.MenuRouter;
import com.smart.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 菜单表 Service
 *
 * @author wf
 * @since 2021-12-31 10:05:07
 */
public interface MenuService extends BaseService<MenuEntity> {

    /**
     * 获取树形结构
     *
     * @param menuEntity bean
     * @return List
     */
    List<MenuEntity> getTree(MenuEntity menuEntity);

    /**
     * 根据角色ID获取菜单
     *
     * @param roleId 角色ID
     * @return List
     */
    List<MenuEntity> findMenuByRoleId(String roleId);

    /**
     * 根据角色ID获取对应路由
     *
     * @param roleId 角色ID
     * @return List
     */
    List<MenuRouter> findRouterByRoleId(String roleId);

    /**
     * 获取树形格式化结构
     *
     * @param menuEntity bean
     * @return List
     */
    List<Map<String, Object>> getTreeFormat(MenuEntity menuEntity);

    /**
     * 树形结构格式化带按钮
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    List<Map<String, Object>> getButtonTreeFormat(MenuEntity menuEntity);

    /**
     * 树形结构格式化带数据权限
     *
     * @param menuEntity 菜单bean
     * @return String
     */
    List<Map<String, Object>> getScopeTreeFormat(MenuEntity menuEntity);

    /**
     * 根据父级ID获取子级下一个排序号
     *
     * @param parentId 父级ID
     * @return int
     */
    int findNextOrderByParentId(String parentId);

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    boolean updateStatus(MenuEntity entity);
}

