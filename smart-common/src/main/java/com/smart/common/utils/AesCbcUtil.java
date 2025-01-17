package com.smart.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;

/**
 * Aes加密解密
 *
 * @author wf
 * @version 1.0.0
 * @since 2022/7/28
 */
public class AesCbcUtil {

    static {
        //BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * AES解密
     *
     * @param data           //密文，被加密的数据
     * @param key            //秘钥
     * @param iv             //偏移量
     * @param encodingFormat //解密后的结果需要进行的编码
     * @return String
     * @throws Exception 异常
     */
    public static String decrypt(String data, String key, String iv, String encodingFormat) throws Exception {
        //被加密的数据
        byte[] dataByte = Base64.decodeBase64(data);
        //加密秘钥
        byte[] keyByte = Base64.decodeBase64(key);
        //偏移量
        byte[] ivByte = Base64.decodeBase64(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");

        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        parameters.init(new IvParameterSpec(ivByte));
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, spec, parameters);

        byte[] resultByte = cipher.doFinal(dataByte);
        if (null != resultByte && resultByte.length > 0) {
            return new String(resultByte, encodingFormat);
        }
        return null;
    }

}

