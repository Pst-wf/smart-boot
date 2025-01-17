package com.smart.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.utils.AuthUtil;
import com.smart.entity.system.UserConfigEntity;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.UserConfigService;
import com.smart.system.dao.UserConfigDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户配置 ServiceImpl
 *
 * @author wf
 * @since 2024-11-05 10:18:28
 */
@Service("userConfigService")
@Transactional(rollbackFor = Exception.class)
public class UserConfigServiceImpl extends BaseServiceImpl<UserConfigDao, UserConfigEntity> implements UserConfigService {
    /**
     * 保存配置
     *
     * @param jsonObject 用户配置
     * @return boolean
     */
    @Override
    public boolean saveConfig(JSONObject jsonObject) {
        UserConfigEntity one = super.getOne(new LambdaQueryWrapper<UserConfigEntity>().eq(UserConfigEntity::getUserId, AuthUtil.getUserId()));
        if (one == null) {
            one = new UserConfigEntity();
            one.setUserId(AuthUtil.getUserId());
        }
        one.setConfigValue(jsonObject.toJSONString());
        return super.saveOrUpdate(one);
    }
    /**
     * 保存 naive-ui 用户配置
     *
     * @param jsonObject 用户配置
     * @return boolean
     */
    @Override
    public boolean saveNaiveUiConfig(JSONObject jsonObject) {
        UserConfigEntity one = super.getOne(new LambdaQueryWrapper<UserConfigEntity>().eq(UserConfigEntity::getUserId, AuthUtil.getUserId()));
        if (one == null) {
            one = new UserConfigEntity();
            one.setUserId(AuthUtil.getUserId());
        }
        one.setNaiveUiConfigValue(jsonObject.toJSONString());
        return super.saveOrUpdate(one);
    }
    /**
     * 保存国际化
     *
     * @param jsonObject 国际化配置
     * @return boolean
     */
    @Override
    public boolean saveLangConfig(JSONObject jsonObject) {
        UserConfigEntity one = super.getOne(new LambdaQueryWrapper<UserConfigEntity>().eq(UserConfigEntity::getUserId, AuthUtil.getUserId()));
        if (one == null) {
            one = new UserConfigEntity();
            one.setUserId(AuthUtil.getUserId());
        }
        one.setLangValue(jsonObject.getString("lang"));
        return super.saveOrUpdate(one);
    }

    /**
     * 保存快捷入口
     *
     * @param array 快捷入口配置
     * @return boolean
     */
    @Override
    public boolean saveQuickEntry(JSONArray array) {
        if (array == null) {
            throw new SmartException("快捷入口设置获取失败！");
        }
        UserConfigEntity one = super.getOne(new LambdaQueryWrapper<UserConfigEntity>().eq(UserConfigEntity::getUserId, AuthUtil.getUserId()));
        if (one == null) {
            one = new UserConfigEntity();
            one.setUserId(AuthUtil.getUserId());
        }
        one.setQuickEntryValue(array.toJSONString());
        return super.saveOrUpdate(one);
    }

    /**
     * 保存快捷入口
     *
     * @param array 快捷入口配置
     * @return boolean
     */
    @Override
    public boolean saveNaiveUiQuickEntry(JSONArray array) {
        if (array == null) {
            throw new SmartException("快捷入口设置获取失败！");
        }
        UserConfigEntity one = super.getOne(new LambdaQueryWrapper<UserConfigEntity>().eq(UserConfigEntity::getUserId, AuthUtil.getUserId()));
        if (one == null) {
            one = new UserConfigEntity();
            one.setUserId(AuthUtil.getUserId());
        }
        one.setNaiveUiQuickEntryValue(array.toJSONString());
        return super.saveOrUpdate(one);
    }
}