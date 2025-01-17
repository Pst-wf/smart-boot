package com.smart.auth.support;

import com.smart.common.utils.DigestUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码加密
 *
 * @author wf
 */
public class SmartPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtil.hex((String) rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }

}
