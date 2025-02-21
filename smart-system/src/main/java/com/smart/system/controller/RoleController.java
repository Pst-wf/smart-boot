package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.system.ButtonsEntity;
import com.smart.entity.system.MenuEntity;
import com.smart.entity.system.RoleEntity;
import com.smart.entity.system.ScopeEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.ButtonsService;
import com.smart.service.system.MenuService;
import com.smart.service.system.RoleService;
import com.smart.service.system.ScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 角色 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @Autowired
    MenuService menuService;
    @Autowired
    ButtonsService buttonsService;
    @Autowired
    ScopeService scopeService;

    /**
     * 列表
     *
     * @param roleEntity 角色bean
     * @return String
     */
    @GetMapping("/page")
    public String page(RoleEntity roleEntity) {
        return Result.data(roleService.findPage(roleEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param roleEntity 角色bean
     * @return String
     */
    @GetMapping("/list")
    public String list(RoleEntity roleEntity) {
        return Result.data(roleService.findList(roleEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        RoleEntity result = roleService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param roleEntity 角色bean
     * @return String
     */
    @HasPermission("role:add")
    @PostMapping("/save")
    @SaveLog(module = "角色管理", type = LogType.ADD)
    public String save(@RequestBody RoleEntity roleEntity) {
        RoleEntity result = roleService.saveEntity(roleEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param roleEntity 角色bean
     * @return String
     */
    @HasPermission("role:update")
    @PostMapping("/update")
    @SaveLog(module = "角色管理", type = LogType.UPDATE)
    public String update(@RequestBody RoleEntity roleEntity) {
        RoleEntity result = roleService.updateEntity(roleEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param roleEntity 角色bean
     * @return String
     */
    @HasPermission("role:delete")
    @PostMapping("/delete")
    @SaveLog(module = "角色管理", type = LogType.DELETE)
    public String delete(@RequestBody RoleEntity roleEntity) {
        return Result.status(roleService.delete(roleEntity));
    }

    /**
     * 获取角色分配的菜单
     *
     * @param roleId 角色Id
     * @return String
     */
    @GetMapping("/roleMenus")
    public String roleMenus(String roleId) {
        List<MenuEntity> list = menuService.findMenuByRoleId(roleId);
        return Result.data(list.stream().map(MenuEntity::getId).collect(Collectors.toList()));
    }

    /**
     * 保存角色分配的菜单
     *
     * @param roleEntity – 角色bean
     * @return String
     */
    @HasPermission("role:update")
    @PostMapping("/setRoleMenus")
    public String setRoleMenus(@RequestBody RoleEntity roleEntity) {
        return Result.status(roleService.setRoleMenus(roleEntity));
    }

    /**
     * 获取角色分配的菜单
     *
     * @param roleId 角色Id
     * @return String
     */
    @GetMapping("/roleButtons")
    public String roleButtons(String roleId) {
        List<ButtonsEntity> list = buttonsService.findButtonsByRoleId(roleId);
        return Result.data(list.stream().map(ButtonsEntity::getId).collect(Collectors.toList()));
    }

    /**
     * 保存角色分配的按钮
     *
     * @param roleEntity – 角色bean
     * @return String
     */
    @HasPermission("role:update")
    @PostMapping("/setRoleButtons")
    public String setRoleButtons(@RequestBody RoleEntity roleEntity) {
        return Result.status(roleService.setRoleButtons(roleEntity));
    }


    /**
     * 获取角色分配的数据权限
     *
     * @param roleId 角色Id
     * @return String
     */
    @GetMapping("/roleScopes")
    public String roleScopes(String roleId) {
        List<ScopeEntity> list = scopeService.findScopesByRoleId(roleId);
        return Result.data(list.stream().map(ScopeEntity::getId).collect(Collectors.toList()));
    }

    /**
     * 保存角色分配的数据权限
     *
     * @param roleEntity – 角色bean
     * @return String
     */
    @HasPermission("role:update")
    @PostMapping("/setRoleScopes")
    public String setRoleScopes(@RequestBody RoleEntity roleEntity) {
        return Result.status(roleService.setRoleScopes(roleEntity));
    }

    /**
     * 修改状态
     *
     * @param entity bean实体
     * @return String
     */
    @HasPermission("role:update")
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestBody RoleEntity entity) {
        return Result.status(roleService.updateStatus(entity));
    }
}
