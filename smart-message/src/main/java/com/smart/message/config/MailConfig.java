package com.smart.message.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.IOUtil;
import com.smart.common.utils.StringUtil;
import com.smart.service.system.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class MailConfig {

    @Autowired
    ConfigService configService;

    @Bean
    public JavaMailSenderImpl javaMailService() {
        String emailConfig = configService.getConfig("sys_email_config");
        if (StringUtil.isBlank(emailConfig)) {
            log.error("获取邮箱配置信息失败，使用默认邮箱配置");
            emailConfig = IOUtil.getValue("json/email.json");
        }
        JSONObject jsonObject = JSON.parseObject(emailConfig);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(jsonObject.getString("host"));
        mailSender.setPort(jsonObject.getInteger("port"));
        mailSender.setUsername(jsonObject.getString("username"));
        mailSender.setPassword(jsonObject.getString("password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
