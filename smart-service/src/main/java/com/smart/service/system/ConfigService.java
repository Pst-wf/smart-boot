package com.smart.service.system;

import com.alibaba.fastjson.JSONObject;
import com.smart.entity.system.ConfigEntity;
import com.smart.mybatis.service.BaseService;

import java.util.Map;

/**
 * 系统配置 Service
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
public interface ConfigService extends BaseService<ConfigEntity> {
    /**
     * 获取对应的配置信息
     *
     * @param key 键
     * @return ConfigEntity
     */
    String getConfig(String key);

    /**
     * 通过keys获取配置
     *
     * @param keys 配置keys
     * @return Map
     */
    Map<String, String> getConfigByKeys(String keys);

    /**
     * 获取系统配置Map
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    Map<String, String> getSystemConfigMap();

    /**
     * 通过 key 更新 value
     *
     * @param configEntity 参数0
     * @return boolean
     */
    boolean updateByKey(ConfigEntity configEntity);

    /**
     * 获取token有效期
     *
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    Map<String, Object> getTokenExpiration();

    /**
     * 修改token有效期
     *
     * @param jsonObject 参数0
     * @return boolean
     */
    boolean updateTokenByKey(JSONObject jsonObject);
}

