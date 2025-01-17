package com.smart.auth.provider;

import com.smart.auth.service.SmartUserDetailsService;
import com.smart.auth.token.WechatAuthenticationToken;
import lombok.Setter;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Map;

@Setter
public class WechatAuthenticationProvider extends SmartAuthenticationProvider {
    private SmartUserDetailsService smartUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        WechatAuthenticationToken authenticationToken = (WechatAuthenticationToken) authentication;
        Map<String, String> map = (Map<String, String>) authenticationToken.getPrincipal();
        UserDetails user = smartUserDetailsService.loadUserByOpenId(map.get("openId"), map.get("userType"));
        if (user == null) {
            // 判断是否开启自主注册
            if (smartUserDetailsService.checkAutoSignUp()) {
                user = smartUserDetailsService.createFrontUserByWechat(map);
            } else {
                throw new InternalAuthenticationServiceException("用户不存在");
            }
        }

        WechatAuthenticationToken authenticationResult = new WechatAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    protected void doAfterPropertiesSet() {
        Assert.notNull(this.smartUserDetailsService, "SmartUserDetailsService未定义");
    }

    /**
     * 当类型为WechatAuthenticationToken的认证实体进入时才走此Provider
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}