package com.smart.common.cache.listener;

import com.smart.common.cache.RedisFactory;
import com.smart.common.cache.callback.RedisCallback;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Arrays;
import java.util.Map;

import static com.smart.common.cache.constant.RedisProvider.getRedisListenerKeyMap;

/**
 * redis 过期监听
 *
 * @author wf
 * @since 2024-12-10
 */
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        Map<String, String[]> redisListenerKeyMap = getRedisListenerKeyMap();
        for (String listenerKey : redisListenerKeyMap.keySet()) {
            String[] keys = redisListenerKeyMap.get(listenerKey);
            // 包含则触发
            long count = Arrays.stream(keys).filter(key -> message.toString().contains(key)).count();
            if (count > 0) {
                RedisCallback redisCallback = RedisFactory.getInvokeStrategy(listenerKey);
                if (redisCallback != null) {
                    redisCallback.callback(message.toString());
                }
            }
        }
    }

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
}
