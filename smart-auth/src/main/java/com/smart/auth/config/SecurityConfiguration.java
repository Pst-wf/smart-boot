/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.smart.auth.config;

import com.smart.auth.provider.SmartAuthenticationProvider;
import com.smart.auth.provider.WechatAuthenticationProvider;
import com.smart.auth.service.SmartUserDetailsService;
import com.smart.auth.support.SmartPasswordEncoderFactories;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security配置
 *
 * @author wf
 */
@Configuration
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    SmartUserDetailsService smartUserDetailsService;

    @Bean
    @Override
    @SneakyThrows
    public AuthenticationManager authenticationManagerBean() {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return SmartPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(name = "smartAuthenticationProvider")
    public AuthenticationProvider smartAuthenticationProvider() {
        SmartAuthenticationProvider smartAuthenticationProvider = new SmartAuthenticationProvider();
        smartAuthenticationProvider.setSmartUserDetailsService(smartUserDetailsService);
        smartAuthenticationProvider.setHideUserNotFoundExceptions(false);
        smartAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return smartAuthenticationProvider;
    }

    @Bean(name = "wechatAuthenticationProvider")
    public AuthenticationProvider wechatAuthenticationProvider() {
        WechatAuthenticationProvider wechatAuthenticationProvider = new WechatAuthenticationProvider();
        wechatAuthenticationProvider.setSmartUserDetailsService(smartUserDetailsService);
        return wechatAuthenticationProvider;
    }

    @Override
    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http.httpBasic().and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(smartAuthenticationProvider());
        auth.authenticationProvider(wechatAuthenticationProvider());
    }
}
