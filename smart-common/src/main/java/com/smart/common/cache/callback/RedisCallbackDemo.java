package com.smart.common.cache.callback;

import com.smart.common.cache.RedisFactory;
import com.smart.common.cache.constant.RedisListenerConstant;
import org.springframework.stereotype.Component;

/**
 * redis 回调demo
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/10/26
 */
@Component
public class RedisCallbackDemo implements RedisCallback {
    /**
     * 回调
     *
     * @param key 键
     */
    @Override
    public void callback(String key) {
        //获取过期的key
        System.out.println("过期键" + key);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedisFactory.register(RedisListenerConstant.REDIS_DEMO_KEY, this);
    }
}
