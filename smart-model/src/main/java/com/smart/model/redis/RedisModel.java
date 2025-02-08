package com.smart.model.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Redis 模型
 *
 * @author wf
 * @version 1.0.0
 * @since 2025-01-10
 */
@Data
public class RedisModel {
    /**
     * 键
     */
    private String key;
    /**
     * 键
     */
    private String dataType;
    /**
     * 值
     */
    private Object value;
    /**
     * 有效期
     */
    private Long expire;
    /**
     * 类型
     */
    private String className;
    /**
     * ID
     */
    private String id;
    /**
     * 父级ID
     */
    private String parentId;
    /**
     * 显示名称
     */
    private String name;
    /**
     * 子集
     */
    private List<RedisModel> children;

    /**
     * 要删除的Key
     */
    @JSONField(serialize = false)
    private List<String> deleteKeys;
}
