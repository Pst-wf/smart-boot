
package com.smart.auth.support;


import com.smart.auth.model.SmartFrontUser;
import com.smart.auth.model.SmartUser;
import com.smart.common.constant.TokenConstant;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

import static com.smart.common.constant.AuthConstant.USER_TYPE_APP;
import static com.smart.common.constant.AuthConstant.USER_TYPE_PC;
import static com.smart.common.constant.SmartConstant.SYSTEM_ID;

/**
 * jwt返回参数增强
 *
 * @author wf
 */
public class SmartJwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Object principal = authentication.getUserAuthentication().getPrincipal();
        if (principal instanceof SmartUser) {
            SmartUser smartUser = (SmartUser) authentication.getUserAuthentication().getPrincipal();
            Map<String, Object> info = new HashMap<>(16);
            info.put(TokenConstant.USER_ID, smartUser.getUserId());
            info.put(TokenConstant.NICKNAME, smartUser.getNickname());
            info.put(TokenConstant.USERNAME, smartUser.getUsername());
            info.put(TokenConstant.PHONE, smartUser.getPhone());
            info.put(TokenConstant.GENDER, smartUser.getGender());
            info.put(TokenConstant.AVATAR, smartUser.getAvatar());
            info.put(TokenConstant.EMAIL, smartUser.getEmail());
            info.put(TokenConstant.IS_SYS, SYSTEM_ID.equals(smartUser.getUserId()));
            info.put(TokenConstant.IDENTITY_ID, smartUser.getIdentityId());
            info.put(TokenConstant.ROLE_ID, smartUser.getRoleId());
            info.put(TokenConstant.ROLE_NAME, smartUser.getRoleName());
            info.put(TokenConstant.POST_ID, smartUser.getPostId());
            info.put(TokenConstant.POST_NAME, smartUser.getPostName());
            info.put(TokenConstant.DEPT_ID, smartUser.getDeptId());
            info.put(TokenConstant.DEPT_NAME, smartUser.getDeptName());
            info.put(TokenConstant.ORGANIZATION_ID, smartUser.getOrganizationId());
            info.put(TokenConstant.ORGANIZATION_NAME, smartUser.getOrganizationName());
            info.put(TokenConstant.IDENTITY_LIST, smartUser.getIdentityList());
            info.put(TokenConstant.REMARKS, smartUser.getRemarks());
            info.put(TokenConstant.USER_TYPE, USER_TYPE_PC);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
            return accessToken;
        } else if (principal instanceof SmartFrontUser) {
            SmartFrontUser smartFrontUser = (SmartFrontUser) authentication.getUserAuthentication().getPrincipal();
            Map<String, Object> info = new HashMap<>(16);
            info.put(TokenConstant.USER_ID, smartFrontUser.getUserId());
            info.put(TokenConstant.NICKNAME, smartFrontUser.getNickname());
            info.put(TokenConstant.USERNAME, smartFrontUser.getUsername());
            info.put(TokenConstant.PHONE, smartFrontUser.getPhone());
            info.put(TokenConstant.GENDER, smartFrontUser.getGender());
            info.put(TokenConstant.AVATAR, smartFrontUser.getAvatar());
            info.put(TokenConstant.USER_TYPE, USER_TYPE_APP);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
            return accessToken;
        } else {
            return accessToken;
        }
    }
}
