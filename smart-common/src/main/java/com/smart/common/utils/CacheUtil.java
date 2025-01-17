package com.smart.common.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Cache工具类
 *
 * @author wf
 * @version 1.0.0
 * @since 2022/7/28
 */
public class CacheUtil {
    private static CacheManager cacheManager;

    public CacheUtil() {
    }

    private static CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = SpringUtil.getBean(CacheManager.class);
        }

        return cacheManager;
    }

    public static Cache getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }


    public static Object get(@NonNull String cacheName, @NonNull String key) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                return valueWrapper.get();
            }
        }
        return null;
    }

    public static <T> T get(@NonNull String cacheName, @NonNull String key, @NonNull Class<T> type) {
        Cache cache = getCache(cacheName);
        if (cache != null) {
            return cache.get(key, type);
        }
        return null;
    }

    /**
     * 修改缓存
     */
    public static void put(@NonNull String cacheName, @NonNull String key, @NonNull Object value) {
        try {
            getCache(cacheName).put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 删除缓存 (key)
     */
    public static void evict(@NonNull String cacheName, @NonNull String key) {
        getCache(cacheName).evict(key);
    }

    /**
     * 批量删除缓存 (key)
     */
    public static void evictKeys(@NonNull String cacheName, @NonNull List<String> keys) {
        Cache cache = getCache(cacheName);
        for (String key : keys) {
            cache.evict(key);
        }
    }

    /**
     * 清除所有缓存
     */
    public static void clear(String cacheName) {
        if (StringUtil.isNotBlank(cacheName)) {
            getCache(cacheName).clear();
        }
    }
}