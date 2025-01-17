package com.smart.common.cache.constant;

import java.util.HashMap;
import java.util.Map;


/**
 * redis 过期 监听通道
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public class RedisProvider {

    /**
     * 需过期监听的键集合
     */
    private static final String[] REDIS_DEMO_KEY_ARRAY = new String[]{};

    /**
     * 监听过滤 Map
     */
    private static final Map<String, String[]> REDIS_LISTENER_KEY_MAP = new HashMap<>(0);


    static {
        REDIS_LISTENER_KEY_MAP.put(RedisListenerConstant.REDIS_DEMO_KEY, REDIS_DEMO_KEY_ARRAY);
    }

    /**
     * 获取 map
     *
     * @return Map
     */
    public static Map<String, String[]> getRedisListenerKeyMap() {
        return REDIS_LISTENER_KEY_MAP;
    }
}
