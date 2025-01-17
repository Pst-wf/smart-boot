package com.smart.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.*;
import com.smart.entity.system.ConfigEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.ConfigService;
import com.smart.system.dao.ConfigDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置 ServiceImpl
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@Service("configService")
@Transactional(rollbackFor = Exception.class)
public class ConfigServiceImpl extends BaseServiceImpl<ConfigDao, ConfigEntity> implements ConfigService {

    /**
     * 修改
     *
     * @param entity bean实体
     * @return bean
     */
    @Override
    public ConfigEntity updateEntity(ConfigEntity entity) {
        // 删除原缓存
        ConfigEntity config = super.get(entity.getId());
        CacheUtil.evict("config", config.getConfigKey());
        ConfigEntity configEntity = super.updateEntity(entity);
        if (configEntity != null) {
            // 修改缓存
            CacheUtil.put("config", configEntity.getConfigKey(), configEntity);
        }
        return configEntity;
    }

    /**
     * 删除
     *
     * @param entity bean实体
     * @return boolean
     */
    @Override
    public boolean delete(ConfigEntity entity) {
        if (ListUtil.isEmpty(entity.getDeleteIds())) {
            throw new SmartException("至少选择一条删除！");
        }
        List<ConfigEntity> list = super.list(new LambdaQueryWrapper<ConfigEntity>().in(ConfigEntity::getId, entity.getDeleteIds()));
        List<String> keys = list.stream().map(ConfigEntity::getConfigKey).collect(Collectors.toList());
        boolean delete = super.delete(entity);
        if (delete) {
            // 清除配置缓存
            CacheUtil.evictKeys("config", keys);
        }
        return delete;
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(ConfigEntity entity, boolean isAdd) {
        List<ConfigEntity> list = super.list(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, entity.getConfigKey()));
        if (isAdd) {
            if (!list.isEmpty()) {
                throw new SmartException("保存失败，当前KEY已存在！");
            }
        } else {
            if (!list.isEmpty() && !list.get(0).getId().equals(entity.getId())) {
                throw new SmartException("保存失败，当前KEY已存在！");
            }
        }
    }

    /**
     * 获取对应的配置信息
     *
     * @param key 键
     * @return ConfigEntity
     */
    @Override
    public String getConfig(String key) {
        ConfigEntity config = CacheUtil.get("config", key, ConfigEntity.class);
        if (config == null) {
            List<ConfigEntity> list = super.list(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, key));
            if (ListUtil.isNotEmpty(list)) {
                CacheUtil.put("config", list.get(0).getConfigKey(), list.get(0));
                return list.get(0).getConfigValue();
            } else {
                throw new SmartException(key + "的配置不存在！");
            }
        } else {
            return config.getConfigValue();
        }
    }

    /**
     * 获取系统配置Map
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    @Override
    public Map<String, String> getSystemConfigMap() {
        List<ConfigEntity> list = super.list(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getIsSystem, SmartConstant.YES));
        String keys = list.stream().map(ConfigEntity::getConfigKey).collect(Collectors.joining(","));
        return StringUtil.isNotBlank(keys) ? getConfigByKeys(keys) : Collections.emptyMap();
    }

    /**
     * 通过 key 更新 value
     *
     * @param configEntity 参数0
     * @return boolean
     */
    @Override
    public boolean updateByKey(ConfigEntity configEntity) {
        if (StringUtil.isBlank(configEntity.getConfigKey())) {
            throw new SmartException("参数Key不能为空");
        }
        ConfigEntity one = super.getOne(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, configEntity.getConfigKey()));
        if (one == null) {
            throw new SmartException("【" + configEntity.getConfigKey() + "】] 不存在");
        }
        one.setConfigValue(configEntity.getConfigValue());
        boolean b = super.updateById(one);
        if (b) {
            // 修改缓存
            CacheUtil.put("config", one.getConfigKey(), one);
        }
        return b;
    }

    /**
     * 通过keys获取配置
     *
     * @param keys 配置keys
     * @return Map
     */
    @Override
    public Map<String, String> getConfigByKeys(String keys) {
        Map<String, String> map = MapUtil.newHashMap();
        if (StringUtil.isNotBlank(keys)) {
            List<String> keyList = ListUtil.newArrayList(keys.split(","));
            // 去重
            Set<String> keySet = SetUtil.newHashSet(keyList);
            for (String key : keySet) {
                map.put(key, getConfig(key));
            }
            return map;
        }
        return null;
    }

    /**
     * 获取token有效期
     *
     * @return java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Map<String, Object> getTokenExpiration() {
        Map<String, Object> map = MapUtil.newHashMap();
        List<LinkedHashMap<String, Object>> list = baseMapper.getClientByClientId(SmartConstant.SMART_CLIENT_ID);
        if (!list.isEmpty()) {
            LinkedHashMap<String, Object> linkedHashMap = list.get(0);
            map.put("access_token_validity", linkedHashMap.get("access_token_validity"));
            map.put("refresh_token_validity", linkedHashMap.get("refresh_token_validity"));
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * 修改token有效期
     *
     * @param jsonObject 参数0
     * @return boolean
     */
    @Override
    public boolean updateTokenByKey(JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        Integer time = jsonObject.getInteger("value");
        if (StringUtil.isBlank(key) || time == null) {
            throw new SmartException("参数异常");
        }
        baseMapper.updateTokenExpirationByClientId(key, time, SmartConstant.SMART_CLIENT_ID);

        return true;
    }
}