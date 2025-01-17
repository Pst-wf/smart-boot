package com.smart.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 *
 * @author calvin
 * @version 2013-01-15
 */
public class EncodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncodeUtil.class);
    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Hex编码.
     */
    public static String encodeHex(byte[] input) {
        return new String(Hex.encodeHex(input));
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * Base64编码.
     */
    public static String encodeBase64(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    /**
     * Base64编码.
     */
    public static String encodeBase64(String input) {
        if (StringUtil.isBlank(input)) {
            return StringUtil.EMPTY;
        }
        try {
            return new String(Base64.encodeBase64(input.getBytes(DEFAULT_URL_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

//	/**
//	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
//	 */
//	public static String encodeUrlSafeBase64(byte[] input) {
//		return Base64.encodeBase64URLSafe(input);
//	}

    /**
     * Base64解码.
     */
    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input.getBytes());
    }

    /**
     * Base64解码.
     */
    public static String decodeBase64String(String input) {
        if (StringUtil.isBlank(input)) {
            return StringUtil.EMPTY;
        }
        try {
            return new String(Base64.decodeBase64(input.getBytes()), DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return StringUtil.EMPTY;
        }
    }

    /**
     * Base62编码。
     */
    public static String encodeBase62(byte[] input) {
        char[] chars = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
        }
        return new String(chars);
    }

    /**
     * Html 转码.
     */
    public static String encodeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    /**
     * Html 解码.
     */
    public static String decodeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    /**
     * Xml 转码.
     */
    public static String encodeXml(String xml) {
        return StringEscapeUtils.escapeXml10(xml);
    }

    /**
     * Xml 解码.
     */
    public static String decodeXml(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String encodeUrl(String part) {
        return encodeUrl(part, DEFAULT_URL_ENCODING);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String encodeUrl(String part, String encoding) {
        if (part == null) {
            return null;
        }
        try {
            return URLEncoder.encode(part, encoding).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String decodeUrl(String part) {
        return decodeUrl(part, DEFAULT_URL_ENCODING);
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String decodeUrl(String part, String encoding) {
        if (part == null) {
            return null;
        }
        try {
            return URLDecoder.decode(part, encoding);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * URL 解码（两次）, Encode默认为UTF-8.
     */
    public static String decodeUrl2(String part) {
        return decodeUrl(decodeUrl(part));
    }

    // 预编译XSS过滤正则表达式
    private static final List<Pattern> XSS_PATTERNS = ListUtil.newArrayList(
            Pattern.compile("(<\\s*(script|link|style|iframe)([\\s\\S]*?)(>|<\\/\\s*\\1\\s*>))|(</\\s*(script|link|style|iframe)\\s*>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*(href|src)\\s*=\\s*(\"\\s*(javascript|vbscript):[^\"]+\"|'\\s*(javascript|vbscript):[^']+'|(javascript|vbscript):[^\\s]+)\\s*(?=>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s*on[a-z]+\\s*=\\s*(\"[^\"]+\"|'[^']+'|[^\\s]+)\\s*(?=>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(eval\\((.*?)\\)|xpression\\((.*?)\\))", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(javascript:|vbscript:)", Pattern.CASE_INSENSITIVE)
    );

    /**
     * XSS 非法字符过滤，内容以<!--HTML-->开头的用以下规则（保留标签）
     *
     * @author ThinkGem
     */
    public static String xssFilter(String text) {
        String oriValue = StringUtil.trim(text);
        if (text != null) {
            String value = oriValue;
            for (Pattern pattern : XSS_PATTERNS) {
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    value = matcher.replaceAll(StringUtil.EMPTY);
                }
            }
            // 如果开始不是HTML，XML，JSON格式，则再进行HTML的 "、<、> 转码。
            if (!StringUtil.startsWithIgnoreCase(value, "<!--HTML-->")    // HTML
                    && !StringUtil.startsWithIgnoreCase(value, "<?xml ")    // XML
                    && !StringUtil.contains(value, "id=\"FormHtml\"")        // JFlow
                    && !(StringUtil.startsWith(value, "{") && StringUtil.endsWith(value, "}")) // JSON Object
                    && !(StringUtil.startsWith(value, "[") && StringUtil.endsWith(value, "]")) // JSON Array
            ) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < value.length(); i++) {
                    char c = value.charAt(i);
                    switch (c) {
                        case '>':
                            sb.append("＞");
                            break;
                        case '<':
                            sb.append("＜");
                            break;
                        case '\'':
                            sb.append("＇");
                            break;
                        case '\"':
                            sb.append("＂");
                            break;
//					case '&':
//						sb.append("＆");
//						break;
//					case '#':
//						sb.append("＃");
//						break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                value = sb.toString();
            }
            if (logger.isInfoEnabled() && !value.equals(oriValue)) {
                logger.info("xssFilter: {}   <=<=<=   {}", value, text);
            }
            return value;
        }
        return null;
    }

    // 预编译SQL过滤正则表达式
    private static final Pattern sqlPattern = Pattern.compile(
            "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|((extractvalue|updatexml)([\\s]*?)\\()|"
                    + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|case when|sleep|union|load_file)\\b)",
            Pattern.CASE_INSENSITIVE);

    /**
     * SQL过滤，防止注入，传入参数输入有select相关代码，替换空。
     *
     * @author ThinkGem
     */
    public static String sqlFilter(String text) {
        if (text != null) {
            String value = text;
            Matcher matcher = sqlPattern.matcher(value);
            if (matcher.find()) {
                value = matcher.replaceAll(StringUtil.EMPTY);
            }
            if (logger.isWarnEnabled() && !value.equals(text)) {
                logger.info("sqlFilter: {}   <=<=<=   {}", value, text);
                return StringUtil.EMPTY;
            }
            return value;
        }
        return null;
    }
}
