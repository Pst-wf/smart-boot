package com.smart.job.config;

import com.smart.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@Slf4j
public class XxlJobConfig {
    private final Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${server.port}")
    private String appPort;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appName}")
    private String appName;

    @Value("${xxl.job.executor.title}")
    private String title;

    @Value("${xxl.job.executor.address}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.executor.port}")
    private int port;

    @Value("${xxl.logPath}")
    private String logPath;

    @Value("${xxl.job.executor.logRetentionDays}")
    private int logRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.error(">>>>>>>>>>> 任务处理器配置初始化.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        // 修改成本地地址
        String adminAddresses = "http://127.0.0.1:" + appPort;
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppName(appName);
        xxlJobSpringExecutor.setTitle(title);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }

//
//    针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
//
//         1、引入依赖：
//             <dependency>
//                <groupId>org.springframework.cloud</groupId>
//                <artifactId>spring-cloud-commons</artifactId>
//                <version>${version}</version>
//            </dependency>
//
//         2、配置文件，或者容器启动变量
//             spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
//
//         3、获取IP
//             String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
//


}