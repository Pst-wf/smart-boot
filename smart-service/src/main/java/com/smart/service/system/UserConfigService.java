package com.smart.service.system;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smart.entity.system.UserConfigEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 用户配置 Service
 *
 * @author wf
 * @since 2024-11-05 10:18:28
 */
public interface UserConfigService extends BaseService<UserConfigEntity> {
    /**
     * 保存配置
     *
     * @param jsonObject 用户配置
     * @return boolean
     */
    boolean saveConfig(JSONObject jsonObject);
    /**
     * 保存 naive-ui 用户配置
     *
     * @param jsonObject 用户配置
     * @return boolean
     */
    boolean saveNaiveUiConfig(JSONObject jsonObject);
    /**
     * 保存国际化
     *
     * @param jsonObject 国际化配置
     * @return boolean
     */
    boolean saveLangConfig(JSONObject jsonObject);

    /**
     * 保存快捷入口
     *
     * @param array 快捷入口配置
     * @return boolean
     */
    boolean saveQuickEntry(JSONArray array);

    /**
     * 保存快捷入口
     *
     * @param array 快捷入口配置
     * @return boolean
     */
    boolean saveNaiveUiQuickEntry(JSONArray array);
}

