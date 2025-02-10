package com.smart.system.service;

import com.alibaba.fastjson.JSON;
import com.smart.common.constant.SmartConstant;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.NumberUtil;
import com.smart.common.utils.StringUtil;
import com.smart.common.utils.TreeUtil;
import com.smart.model.exception.SmartException;
import com.smart.model.redis.RedisModel;
import com.smart.system.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存操作服务
 *
 * @author wf
 * @version 1.0.0
 * @since 2025-01-10
 */
@Service
public class RedisOptService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询
     *
     * @param key      键
     * @param showType 展示形式
     * @param type     查询模式
     * @return java.util.Set<java.lang.String>
     */
    public List<RedisModel> query(String key, String showType, String type) {
        List<RedisModel> list = new ArrayList<>();
        ScanOptions option = ScanOptions.NONE;
        if (StringUtil.isNotBlank(key)) {
            if (StringUtil.notBlankAndEquals(type, SystemConstant.REDIS_QUERY_TYPE_1)) {
                // 精确查询
                option = ScanOptions.scanOptions().match(key).build();
            } else {
                option = ScanOptions.scanOptions().match("*" + key + "*").build();
            }
        }
        try (Cursor<String> cursor = redisTemplate.scan(option)) {
            while (cursor.hasNext()) {
                String targetKey = cursor.next();
                Long expire = redisTemplate.getExpire(targetKey);
                Object value = redisTemplate.opsForValue().get(targetKey);
                DataType dataType = redisTemplate.type(targetKey);
                RedisModel redisModel = new RedisModel();
                redisModel.setId(targetKey);
                redisModel.setKey(targetKey);
                redisModel.setName(targetKey);
                redisModel.setDataType(dataType.code());
                redisModel.setValue(value);
                if (value != null) {
                    redisModel.setClassName(value.getClass().getName());
                }
                redisModel.setExpire(expire);
                list.add(redisModel);
            }
        }
        if (StringUtil.notBlankAndEquals(showType, SystemConstant.REDIS_QUERY_SHOW_TYPE_2)) {
            return list;
        }
        List<RedisModel> res = new ArrayList<>();
        // 处理树形
        for (RedisModel redisModel : list) {
            prepareToTree(res, redisModel);
        }
        List<RedisModel> collect1 = res.stream().filter(item -> StringUtil.isBlank(item.getKey())).collect(Collectors.toList());
        List<RedisModel> collect2 = res.stream().filter(item -> StringUtil.isNotBlank(item.getKey())).sorted(Comparator.comparing(RedisModel::getExpire)).collect(Collectors.toList());
        List<RedisModel> format = ListUtil.unionCollection(collect1, collect2);
        return TreeUtil.buildTree(format);
    }

    /**
     * 查询
     *
     * @param key 键
     * @return java.util.Set<java.lang.String>
     */
    public RedisModel getOne(String key) {
        Boolean b = redisTemplate.hasKey(key);
        if (b) {
            Long expire = redisTemplate.getExpire(key);
            Object value = redisTemplate.opsForValue().get(key);
            DataType dataType = redisTemplate.type(key);
            RedisModel redisModel = new RedisModel();
            redisModel.setId(key);
            redisModel.setKey(key);
            redisModel.setName(key);
            redisModel.setDataType(dataType.code());
            redisModel.setValue(value);
            if (value != null) {
                redisModel.setClassName(value.getClass().getName());
            }
            redisModel.setExpire(expire);
            return redisModel;
        }
        return null;
    }

    /**
     * 新增
     *
     * @param redisModel 参数0
     */
    public void saveOrUpdate(RedisModel redisModel) {
        Object value = redisModel.getValue();
        String className = redisModel.getClassName();
        if (value != null) {
            try {
                Class<?> aClass = Class.forName(className);
                // 转换类型
                if (value instanceof LinkedHashMap) {
                    String jsonString = JSON.toJSONString(value);
                    value = JSON.parseObject(jsonString, aClass);
                } else {
                    // 数字类型
                    if (NumberUtil.isNumeric(value.toString())) {
                        Method valueOfMethod = aClass.getMethod("valueOf", String.class);
                        value = valueOfMethod.invoke(null, value.toString());
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (redisModel.getExpire() != null && redisModel.getExpire() > 0) {
                redisTemplate.opsForValue().set(redisModel.getKey(), value, redisModel.getExpire(), TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(redisModel.getKey(), value);
            }
        } else {
            redisTemplate.delete(redisModel.getKey());
        }

    }

    /**
     * 删除
     *
     * @param redisModel 参数0
     */
    public void delete(RedisModel redisModel) {
        if (ListUtil.isEmpty(redisModel.getDeleteKeys())) {
            throw new SmartException("至少选择一条数据！");
        }
        List<String> deleteKeys = redisModel.getDeleteKeys().stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
        if (!deleteKeys.isEmpty()) {
            redisTemplate.delete(deleteKeys);
        }
    }


    /**
     * 构建树形结构数据
     *
     * @param all        树形结构数据
     * @param redisModel 缓存对象
     */
    private synchronized void prepareToTree(List<RedisModel> all, RedisModel redisModel) {
        String[] split = redisModel.getKey().split(":");
        Set<String> ids = all.stream().map(RedisModel::getId).collect(Collectors.toSet());
        String parentId = SmartConstant.INT_ZERO;
        for (int i = 0; i < split.length; i++) {
            String name = split[i];
            String id = StringUtil.substringToNthOccurrence(redisModel.getKey(), ":", i + 1);
            if (!ids.contains(id)) {
                RedisModel one = new RedisModel();
                one.setId(id);
                one.setParentId(parentId);
                one.setName(name);
                if (i == split.length - 1) {
                    one.setKey(redisModel.getKey());
                    one.setDataType(redisModel.getDataType());
                    one.setValue(redisModel.getValue());
                    one.setClassName(redisModel.getClassName());
                    one.setExpire(redisModel.getExpire());
                }
                all.add(one);
            }
            parentId = id;
        }
    }
}
