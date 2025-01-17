package com.smart.common.cache.callback;

import org.springframework.beans.factory.InitializingBean;

/**
 * Redis回调
 *
 * @author wf
 * @version 1.0.0
 * @since 2024-12-10
 */
public interface RedisCallback extends InitializingBean {
    /**
     * 回调
     *
     * @param key 键
     */
    void callback(String key);
}
