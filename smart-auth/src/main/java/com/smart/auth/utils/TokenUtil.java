package com.smart.auth.utils;

import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.TenantEntity;
import lombok.SneakyThrows;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import static com.smart.common.constant.TokenConstant.HEADER;
import static com.smart.common.constant.TokenConstant.HEADER_PREFIX;
import static com.smart.model.response.r.MessageProperties.USER_HAS_NO_TENANT;
import static com.smart.model.response.r.MessageProperties.USER_HAS_NO_TENANT_PERMISSION;

/**
 * 认证工具类
 *
 * @author wf
 */
public class TokenUtil {
    /**
     * 解码
     */
    @SneakyThrows
    public static String[] extractAndDecodeHeader() {
        if (AuthUtil.getRequest() == null) {
            throw new RuntimeException("获取request失败！");
        }
        String header = AuthUtil.getRequest().getHeader(HEADER);
        if (header == null || !header.startsWith(HEADER_PREFIX)) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Basic令牌解析失败！");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int index = token.indexOf(":");
        if (index == -1) {
            throw new BadCredentialsException("Basic令牌无效!");
        } else {
            return new String[]{token.substring(0, index), token.substring(index + 1)};
        }
    }

    /**
     * 加密
     */
    @SneakyThrows
    public static String generatorDecodeHeader(String id, String secret) {
        if (StringUtil.isNotBlank(id) && StringUtil.isNotBlank(secret)) {
            String val = id + ":" + secret;
            byte[] bytes = val.getBytes(StandardCharsets.UTF_8);
            byte[] encode = Base64.getEncoder().encode(bytes);
            String token = new String(encode, StandardCharsets.UTF_8);
            return HEADER_PREFIX + token;
        } else {
            return null;
        }
    }

    /**
     * 获取请求头中的客户端id
     */
    public static String getClientIdFromHeader() {
        String[] tokens = extractAndDecodeHeader();
        return tokens[0];
    }

    /**
     * 获取token过期时间(次日凌晨3点)
     *
     * @return expire
     */
    public static int getTokenValiditySecond() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 获取refreshToken过期时间
     *
     * @return expire
     */
    public static int getRefreshTokenValiditySeconds() {
        return 60 * 60 * 24 * 15;
    }

    /**
     * 判断租户权限
     *
     * @param tenant 租户信息
     * @return boolean
     */
    public static boolean judgeTenant(TenantEntity tenant) {
        if (tenant == null) {
            throw new UserDeniedAuthorizationException(USER_HAS_NO_TENANT);
        }
        Date expireTime = tenant.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new UserDeniedAuthorizationException(USER_HAS_NO_TENANT_PERMISSION);
        }
        return false;
    }

}
