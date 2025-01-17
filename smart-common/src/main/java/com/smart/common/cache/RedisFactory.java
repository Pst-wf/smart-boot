package com.smart.common.cache;

import com.smart.common.cache.callback.RedisCallback;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis 回调工厂
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/10/26
 */
public class RedisFactory {

    private static final Map<String, RedisCallback> REDIS_CALLBACK_MAP = new HashMap<>(0);

    /**
     * 将业务类型和策略方法注册进map，以备工厂生产时调用
     *
     * @param key      策略类型
     * @param callback 策略类
     */
    public static void register(@NonNull String key, @NonNull RedisCallback callback) {
        REDIS_CALLBACK_MAP.put(key, callback);
    }

    /**
     * 传入业务类型生成对应的策略方法
     *
     * @param key 策略类型
     * @return 策略类
     */
    public static RedisCallback getInvokeStrategy(String key) {
        return REDIS_CALLBACK_MAP.get(key);
    }
}
