package com.smart.auth.config;

import com.smart.auth.granter.SmartTokenGranter;
import com.smart.auth.service.SmartClientDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static com.smart.common.constant.AuthConstant.DEFAULT_FIND_STATEMENT;
import static com.smart.common.constant.AuthConstant.DEFAULT_SELECT_STATEMENT;

/**
 * 认证服务器配置
 *
 * @author wf
 */
@Order
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class SmartAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;

    private AuthenticationManager authenticationManager;

    private UserDetailsService userDetailsService;

    private TokenStore tokenStore;

    private TokenEnhancer jwtTokenEnhancer;

    private JwtAccessTokenConverter jwtAccessTokenConverter;


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //获取自定义tokenGranter
        TokenGranter tokenGranter = SmartTokenGranter.getTokenGranter(authenticationManager, endpoints);

        //配置端点
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenGranter(tokenGranter);

        //扩展token返回结果
        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancerList = new ArrayList<>();
            enhancerList.add(jwtTokenEnhancer);
            enhancerList.add(jwtAccessTokenConverter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
            //jwt增强
            endpoints.tokenEnhancer(tokenEnhancerChain).accessTokenConverter(jwtAccessTokenConverter);
        }
    }

    /**
     * 配置客户端信息
     */
    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        SmartClientDetailsServiceImpl clientDetailsService = new SmartClientDetailsServiceImpl(dataSource);
        clientDetailsService.setSelectClientDetailsSql(DEFAULT_SELECT_STATEMENT);
        clientDetailsService.setFindClientDetailsSql(DEFAULT_FIND_STATEMENT);
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}
