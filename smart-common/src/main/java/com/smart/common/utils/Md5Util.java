package com.smart.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.security.MessageDigest;

/**
 * MD5工具类
 *
 * @author wf
 * @version 2021/3/31 13:08
 */
public class Md5Util {
    /**
     * MD5 加密（盐值）
     *
     * @param password 密码
     * @param username 账号
     * @return 加密结果
     */
    public static String md5(String password, String username) {
        //加密方式
        String hashAlgorithmName = "md5";
        //盐：为了即使相同的密码不同的盐加密后的结果也不同
        ByteSource byteSalt = ByteSource.Util.bytes(username);
        //加密次数
        int hashIterations = 1024;
        return new SimpleHash(hashAlgorithmName, password, byteSalt, hashIterations).toHex();
    }

    /**
     * 生成32位小写的md5加密
     */
    public static String lowMD5_32(String plainText) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(plainText.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            plainText = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return plainText;
    }

    /**
     * 生成16位小写的md5加密
     */
    public static String lowMD5_16(String plainText) {
        String str = lowMD5_32(plainText);
        return str.substring(8, 24);
    }

    /**
     * 生成16位大写的md5加密
     */
    public static String upperMD5_16(String plainText) {
        return lowMD5_16(plainText).toUpperCase();
    }

    /**
     * 生成32位大写的md5加密
     */
    public static String upperMD5_32(String plainText) {
        return lowMD5_32(plainText).toUpperCase();
    }
}
