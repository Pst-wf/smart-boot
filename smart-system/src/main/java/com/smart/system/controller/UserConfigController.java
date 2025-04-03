package com.smart.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.utils.AuthUtil;
import com.smart.entity.system.UserConfigEntity;
import com.smart.model.response.r.Result;
import com.smart.service.system.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户配置 Controller
 *
 * @author wf
 * @since 2024-11-05 10:18:28
 */
@RestController
@RequestMapping("/system/userConfig")
public class UserConfigController {
    @Autowired
    UserConfigService userConfigService;

    /**
     * 信息
     *
     * @return String
     */
    @GetMapping("/get")
    public String get() {
        return Result.data(Db.lambdaQuery(UserConfigEntity.class).eq(UserConfigEntity::getUserId, AuthUtil.getUserId()).one());
    }

    /**
     * 保存配置
     *
     * @param jsonObject 用户配置
     * @return String
     */
    @PostMapping("/save")
    public String save(@RequestBody JSONObject jsonObject) {
        return Result.status(userConfigService.saveConfig(jsonObject));
    }

    /**
     * 保存 naive-ui 用户配置
     *
     * @param jsonObject 用户配置
     * @return String
     */
    @PostMapping("/saveNaive")
    public String saveNaive(@RequestBody JSONObject jsonObject) {
        return Result.status(userConfigService.saveNaiveUiConfig(jsonObject));
    }

    /**
     * 保存国际化
     *
     * @param jsonObject 用户配置
     * @return String
     */
    @PostMapping("/saveLang")
    public String saveLang(@RequestBody JSONObject jsonObject) {
        return Result.status(userConfigService.saveLangConfig(jsonObject));
    }

    /**
     * 保存快捷入口
     *
     * @param jsonObject 用户配置
     * @return String
     */
    @PostMapping("/saveQuickEntry")
    public String saveQuickEntry(@RequestBody JSONObject jsonObject) {
        return Result.status(userConfigService.saveQuickEntry(jsonObject.getJSONArray("params")));
    }

    /**
     * 保存快捷入口
     *
     * @param jsonObject 用户配置
     * @return String
     */
    @PostMapping("/saveNaiveQuick")
    public String saveNaiveUiQuickEntry(@RequestBody JSONObject jsonObject) {
        return Result.status(userConfigService.saveNaiveUiQuickEntry(jsonObject.getJSONArray("params")));
    }

}
