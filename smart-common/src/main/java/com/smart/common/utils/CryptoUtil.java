package com.smart.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Description: 配合前端CryptoJS实现加密、解密工作。
 * CryptoJS 是一个 JavaScript 库，提供了一系列密码学函数和工具，用于加密、解密、生成摘要等任务。
 * 它支持多种加密算法，包括常见的对称加密算法（如 AES、DES）和非对称加密算法（如 RSA）。
 *
 * @author Administrator
 * @date 2024/07/21
 */
public class CryptoUtil {

    private final static String IV = "1234567890123456";
    //需要前端与后端配置一致

    private final static String KEY = "1234567890123456";

    /**
     * 加密算法，使用默认的IV、KEY
     */
    public static String encrypt(String content) {
        return encrypt(content, KEY, IV);
    }

    /**
     * 解密算法，使用默认的IV、KEY
     */
    public static String decrypt(String content) {
        return decrypt(content, KEY, IV);
    }

    /**
     * 加密方法
     */
    public static String encrypt(String content, String key, String iv) {
        try {
            // "算法/模式/补码方式"NoPadding PkcsPadding
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = content.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密算法异常 CryptoUtil encrypt()加密方法，异常信息：" + e.getMessage());
        }
    }

    /**
     * 解密方法
     */
    public static String decrypt(String content, String key, String iv) {
        try {
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(content);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (Exception e) {
           return null;
        }
    }
}
