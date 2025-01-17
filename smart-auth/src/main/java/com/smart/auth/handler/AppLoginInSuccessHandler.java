
package com.smart.auth.handler;

import com.smart.auth.utils.TokenUtil;
import com.smart.common.utils.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * APP登录成功处理器
 *
 * @author wf
 */
@Slf4j
@AllArgsConstructor
@Component("appLoginInSuccessHandler")
public class AppLoginInSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private PasswordEncoder passwordEncoder;

    private ClientDetailsService clientDetailsService;

    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        log.info("【AppLoginInSuccessHandler】 onAuthenticationSuccess authentication={}", authentication);

        String[] tokens = TokenUtil.extractAndDecodeHeader();
        if (tokens.length != 2) {
            throw new UnapprovedClientAuthenticationException("client对应的配置信息不存在");
        }
        String clientId = tokens[0];
        String clientSecret = tokens[1];

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId 对应的配置信息不存在" + clientId);
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret 不匹配" + clientId);
        }

        TokenRequest tokenRequest = new TokenRequest(new HashMap<>(16), clientId, clientDetails.getScope(), "app");
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(ObjectUtil.toJSONString(token));
    }

}
