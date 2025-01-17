
package com.smart.common.cache.listener;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis监听配置
 *
 * @author shq
 * @since 2020-02-27
 */

@Configuration(proxyBeanMethods = false)
public class RedisListenerConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    KeyExpirationEventMessageListener redisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        return new RedisKeyExpirationListener(listenerContainer);
    }
}

