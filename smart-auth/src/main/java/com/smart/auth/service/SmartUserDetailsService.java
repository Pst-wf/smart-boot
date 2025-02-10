package com.smart.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * 自定义UserDetailService
 *
 * @author wf
 */
public interface SmartUserDetailsService extends UserDetailsService {
    /**
     * 获取用户信息
     *
     * @param username 账号
     * @param userType 用户类型
     * @return 用户信息
     * @throws UsernameNotFoundException 异常
     */
    UserDetails loadUserByUsername(String username, String userType) throws UsernameNotFoundException;

    /**
     * 获取用户信息
     *
     * @param openId   微信OpenId
     * @param userType 用户类型
     * @return 用户信息
     * @throws UsernameNotFoundException 异常
     */
    UserDetails loadUserByOpenId(String openId, String userType) throws UsernameNotFoundException;

    /**
     * 创建用户（微信）
     *
     * @param map 参数
     * @return 用户信息
     * @throws UsernameNotFoundException 异常
     */
    UserDetails createFrontUserByWechat(Map<String, String> map) throws UsernameNotFoundException;

    /**
     * 检查是否开启自主注册
     *
     * @return boolean
     */
    boolean checkAutoSignUp();

    /**
     * 记录登录错误次数
     *
     * @param username 账号
     */
    String recordLockTimes(String username);

    /**
     * 校验账号是否被锁定
     *
     * @param username 账号
     */
    String checkAccountLock(String username);
}
