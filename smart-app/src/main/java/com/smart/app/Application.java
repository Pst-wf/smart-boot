package com.smart.app;

import com.smart.model.response.r.MessageProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author wf
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.smart")
@MapperScan("com.smart.**.*.dao")
@EnableConfigurationProperties({MessageProperties.class})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
