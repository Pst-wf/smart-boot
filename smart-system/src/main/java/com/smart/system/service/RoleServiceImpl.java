package com.smart.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.smart.common.utils.CacheUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.*;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.*;
import com.smart.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.smart.system.constant.SystemConstant.MENU_TYPE_2;

/**
 * 角色 ServiceImpl
 *
 * @author wf
 * @since 2022-01-01 17:28:23
 */
@Service("roleService")
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends BaseServiceImpl<RoleDao, RoleEntity> implements RoleService {

    @Autowired
    MenuService menuService;
    @Autowired
    RoleMenuService roleMenuService;
    @Autowired
    IdentityInfoService identityInfoService;
    @Autowired
    RoleScopeService roleScopeService;
    @Autowired
    RoleButtonsServiceImpl roleButtonsService;
    @Autowired
    ButtonsService buttonsService;
    @Autowired
    private ScopeServiceImpl scopeService;

    /**
     * 分页
     *
     * @param entity bean实体
     * @return Page
     */
    @Override
    public Page<RoleEntity> findPage(RoleEntity entity) {
        Page<RoleEntity> page = super.findPage(entity);
        page.getResult().forEach(x -> {
            List<MenuEntity> menus = menuService.findMenuByRoleId(x.getId());
            List<MenuEntity> collect = menus.stream().filter(i -> StringUtil.notBlankAndEquals(i.getMenuType(), MENU_TYPE_2)).sorted(Comparator.comparing(MenuEntity::getMenuType).thenComparing(MenuEntity::getOrder)).collect(Collectors.toList());
            List<ButtonsEntity> buttons = buttonsService.findButtonsByRoleId(x.getId());
            collect.forEach(menu -> {
                menu.setButtons(buttons.stream()
                        .filter(button -> menu.getId().equals(button.getMenuId()))
                        .sorted(Comparator.comparing(ButtonsEntity::getSort))
                        .collect(Collectors.toList()));
            });
            x.setMenus(collect);
        });
        return page;
    }

    /**
     * 删除之前处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void beforeDelete(RoleEntity entity, boolean isReal) {
        //验证该角色是否有人使用
        long count = identityInfoService.count(new LambdaQueryWrapper<IdentityEntity>().in(IdentityEntity::getRoleId, entity.getDeleteIds()));
        if (count > 0) {
            throw new SmartException("要删除的角色已被使用，不可删除！");
        }
    }

    /**
     * 删除之后处理
     *
     * @param entity bean 实体
     * @param isReal 是否物理删除
     */
    @Override
    public void afterDelete(RoleEntity entity, boolean isReal) {
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenuEntity>().in(RoleMenuEntity::getRoleId, entity.getDeleteIds()));
    }

    /**
     * 保存角色菜单
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    @Override
    public boolean setRoleMenus(RoleEntity roleEntity) {
        //移除原来的角色菜单
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenuEntity>().in(RoleMenuEntity::getRoleId, roleEntity.getId()));
        if (ListUtil.isNotEmpty(roleEntity.getMenuIds())) {
            List<RoleMenuEntity> roleMenus = new ArrayList<>();
            roleEntity.getMenuIds().forEach(x -> {
                RoleMenuEntity roleMenu = new RoleMenuEntity();
                roleMenu.setMenuId(x);
                roleMenu.setRoleId(roleEntity.getId());
                roleMenus.add(roleMenu);
            });
            return roleMenuService.saveBatch(roleMenus);
        }
        return true;
    }

    /**
     * 保存角色按钮
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    @Override
    public boolean setRoleButtons(RoleEntity roleEntity) {
        // 清空缓存
        CacheUtil.evict("buttons", roleEntity.getId());
        //移除原来的角色按钮
        roleButtonsService.remove(new LambdaQueryWrapper<RoleButtonsEntity>().in(RoleButtonsEntity::getRoleId, roleEntity.getId()));
        if (ListUtil.isNotEmpty(roleEntity.getButtonIds())) {
            List<ButtonsEntity> buttons = buttonsService.listByIds(roleEntity.getButtonIds());
            if (ListUtil.isNotEmpty(buttons)) {
                List<RoleButtonsEntity> roleButtonsList = new ArrayList<>();
                buttons.forEach(x -> {
                    RoleButtonsEntity roleButtons = new RoleButtonsEntity();
                    roleButtons.setButtonCode(x.getCode());
                    roleButtons.setRoleId(roleEntity.getId());
                    roleButtonsList.add(roleButtons);
                });
                return roleButtonsService.saveBatch(roleButtonsList);
            }
        }
        return true;
    }

    /**
     * 保存角色数据权限
     *
     * @param roleEntity 角色Bean
     * @return boolean
     */
    @Override
    public boolean setRoleScopes(RoleEntity roleEntity) {
        // 清除缓存
        CacheUtil.evict("scope", roleEntity.getId());
        //移除原来的角色数据权限
        roleScopeService.remove(new LambdaQueryWrapper<RoleScopeEntity>().in(RoleScopeEntity::getRoleId, roleEntity.getId()));
        if (ListUtil.isNotEmpty(roleEntity.getScopeIds())) {
            List<ScopeEntity> scopes = scopeService.listByIds(roleEntity.getScopeIds());
            if (ListUtil.isNotEmpty(scopes)) {
                List<RoleScopeEntity> roleScopeList = new ArrayList<>();
                scopes.forEach(x -> {
                    RoleScopeEntity roleScope = new RoleScopeEntity();
                    roleScope.setScopeId(x.getId());
                    roleScope.setRoleId(roleEntity.getId());
                    roleScopeList.add(roleScope);
                });
                return roleScopeService.saveBatch(roleScopeList);
            }
        }
        return true;
    }

    /**
     * 获取角色按钮
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    @Cacheable(cacheNames = "buttons", key = "#roleId", unless = "#result == null")
    public List<ButtonsEntity> getButtons(String roleId) {
        return buttonsService.findButtonsByRoleId(roleId);
    }

    /**
     * 获取角色数据权限
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    @Cacheable(cacheNames = "scope", key = "#roleId", unless = "#result == null")
    public List<ScopeEntity> getScopes(String roleId) {
        return scopeService.findScopesByRoleId(roleId);
    }
}