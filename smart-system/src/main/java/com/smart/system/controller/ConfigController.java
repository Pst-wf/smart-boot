package com.smart.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.ConfigEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统配置 Controller
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@RestController
@RequestMapping("/system/config")
public class ConfigController {
    @Autowired
    ConfigService configService;

    /**
     * 列表
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @GetMapping("/page")
    public String page(ConfigEntity configEntity) {
        return Result.data(configService.findPage(configEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        ConfigEntity result = configService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @HasPermission("config:add")
    @PostMapping("/save")
    @SaveLog(module = "系统配置管理", type = LogType.ADD)
    public String save(@RequestBody ConfigEntity configEntity) {
        ConfigEntity result = configService.saveEntity(configEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @HasPermission("config:update")
    @PostMapping("/update")
    @SaveLog(module = "系统配置管理", type = LogType.UPDATE)
    public String update(@RequestBody ConfigEntity configEntity) {
        ConfigEntity result = configService.updateEntity(configEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @HasPermission("config:delete")
    @PostMapping("/delete")
    @SaveLog(module = "系统配置管理", type = LogType.DELETE)
    public String delete(@RequestBody ConfigEntity configEntity) {
        return Result.status(configService.delete(configEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @GetMapping("/list")
    public String list(ConfigEntity configEntity) {
        if (StringUtil.isBlank(configEntity.getSortField()) && StringUtil.isBlank(configEntity.getSortOrder())) {
            configEntity.setSortField(ConfigEntity.Fields.configKey);
            configEntity.setSortOrder("ASC");
        }
        return Result.data(configService.findList(configEntity));
    }

    /**
     * 获取系统配置Map
     *
     * @return String
     */
    @GetMapping("/getSystemConfigMap")
    public String getSystemConfigMap() {
        return Result.data(configService.getSystemConfigMap());
    }

    /**
     * 通过keys获取配置
     *
     * @param keys 配置keys
     * @return String
     */
    @GetMapping("/getConfigByKeys")
    public String getConfigByKeys(@RequestParam String keys) {
        return Result.data(configService.getConfigByKeys(keys));
    }

    /**
     * 删除
     *
     * @param configEntity 系统配置bean
     * @return String
     */
    @HasPermission("config:update")
    @PostMapping("/updateByKey")
    public String updateByKey(@RequestBody ConfigEntity configEntity) {
        return Result.status(configService.updateByKey(configEntity));
    }

    /**
     * 获取token有效期
     *
     * @return String
     */
    @GetMapping("/getTokenExpiration")
    public String getTokenExpiration() {
        return Result.data(configService.getTokenExpiration());
    }

    /**
     * 删除
     *
     * @param jsonObject 修改token有效期
     * @return String
     */
    @HasPermission("config:update")
    @PostMapping("/updateTokenByKey")
    public String updateTokenByKey(@RequestBody JSONObject jsonObject) {
        return Result.status(configService.updateTokenByKey(jsonObject));
    }


}
