package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.CacheUtil;
import com.smart.entity.system.ScopeEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.ScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 数据权限 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/scope")
public class ScopeController {
    @Autowired
    ScopeService scopeService;

    /**
     * 列表
     *
     * @param scopeEntity 数据权限bean
     * @return String
     */
    @GetMapping("/page")
    public String page(ScopeEntity scopeEntity) {
        return Result.data(scopeService.findPage(scopeEntity));
    }

    /**
     * 列表 (不分页)
     *
     * @param scopeEntity 数据权限bean
     * @return String
     */
    @GetMapping("/list")
    public String list(ScopeEntity scopeEntity) {
        return Result.data(scopeService.findList(scopeEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        ScopeEntity result = scopeService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param scopeEntity 数据权限bean
     * @return String
     */
    @HasPermission("scope:add")
    @PostMapping("/save")
    @SaveLog(module = "数据权限管理", type = LogType.ADD)
    public String save(@RequestBody ScopeEntity scopeEntity) {
        ScopeEntity result = scopeService.saveEntity(scopeEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param scopeEntity 数据权限bean
     * @return String
     */
    @HasPermission("scope:update")
    @PostMapping("/update")
    @SaveLog(module = "数据权限管理", type = LogType.UPDATE)
    public String update(@RequestBody ScopeEntity scopeEntity) {
        ScopeEntity result = scopeService.updateEntity(scopeEntity);
        if (result != null) {
            CacheUtil.clear("scope");
            return Result.data(result);
        } else {
            return Result.fail();
        }
    }

    /**
     * 删除
     *
     * @param scopeEntity 数据权限bean
     * @return String
     */
    @HasPermission("scope:delete")
    @PostMapping("/delete")
    @SaveLog(module = "数据权限管理", type = LogType.DELETE)
    public String delete(@RequestBody ScopeEntity scopeEntity) {
        boolean b = scopeService.delete(scopeEntity);
        if (b) {
            CacheUtil.clear("scope");
            return Result.success();
        } else {
            return Result.fail();
        }
    }
}
