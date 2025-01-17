package com.smart.auth.support;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义密码工厂
 *
 * @author wf
 * @since 5.0
 */
public class SmartPasswordEncoderFactories {

    /**
     * Creates a {@link DelegatingPasswordEncoder} with default mappings. Additional
     * mappings may be added and the encoding will be updated to conform with best
     * practices. However, due to the nature of {@link DelegatingPasswordEncoder} the
     * updates should not impact users. The mappings current are:
     *
     * <ul>
     * <li>smart - {@link SmartPasswordEncoder} (sha1(md5("password")))</li>
     * <li>bcrypt - {@link BCryptPasswordEncoder} (Also used for encoding)</li>
     * <li>noop - {@link SmartNoOpPasswordEncoder}</li>
     * <li>pbkdf2 - {@link Pbkdf2PasswordEncoder}</li>
     * <li>scrypt - {@link SCryptPasswordEncoder}</li>
     * </ul>
     *
     * @return the {@link PasswordEncoder} to use
     */
    public static PasswordEncoder createDelegatingPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>(16);
        encoders.put("smart", new SmartPasswordEncoder());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", SmartNoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        /*此处是对前台传过来的密码进行二次处理，当前使用的是不处理*/
        /*修改下面的key可以使用其他加密规则*/
        return new DelegatingPasswordEncoder("noop", encoders);
    }

    private SmartPasswordEncoderFactories() {
    }

}
