
package com.smart.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author wf
 * @version 2022-01-15 00:21:17
 */
public class JwtUtil {
    public static String SIGN_KEY = "Smart";
    public static String BEARER = "bearer";
    public static Integer AUTH_LENGTH = 7;
    public static String BASE64_SECURITY;

    public JwtUtil() {
    }

    /**
     * 获取token
     *
     * @param auth header中的auth
     * @return String
     */
    public static String getToken(String auth) {
        if (auth != null && auth.length() > AUTH_LENGTH) {
            String headStr = auth.substring(0, 6).toLowerCase();
            if (headStr.compareTo(BEARER) == 0) {
                auth = auth.substring(7);
            }
            return auth;
        } else {
            return null;
        }
    }

    /**
     * 创建token
     *
     * @param id
     * @param map
     * @param expDate
     * @return
     */
    public static String createJWT(String id, Map<String, Object> map, Date expDate) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder()
                .setId(id) //唯一的ID
                .addClaims(map)
                .signWith(signatureAlgorithm, Base64.getDecoder().decode(BASE64_SECURITY)) //使用HS256对称加密算法签 名, 第二个参数为秘钥
                .setExpiration(expDate);// 设置过期时间
        return builder.compact();
    }

    /**
     * 解析Token
     *
     * @param token 令牌
     * @return Claims
     */
    public static Claims parseJwt(String token) {
        try {
            return Jwts.parser().setSigningKey(Base64.getDecoder().decode(BASE64_SECURITY)).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        BASE64_SECURITY = Base64.getEncoder().encodeToString(SIGN_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        Claims claims = parseJwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXB0TmFtZSI6IumDqOmXqEEiLCJvcmdhbml6YXRpb25OYW1lIjoiUmFkaXNo5YWs5Y-4IiwidXNlcl9uYW1lIjoiYWRtaW4iLCJyb2xlSWQiOiIxNDkxOTU4MTgzMTM1MTY2NDY2IiwiZGVwdElkIjoiMTY1MDcwOTU2OTI2Mjg2NjQzMyIsImF2YXRhciI6Imh0dHBzOi8vcHN0LXRlc3Qub3NzLWNuLWJlaWppbmcuYWxpeXVuY3MuY29tL3BzdC10ZXN0L2ZpbGUvMi03NjhhZjllMTNlMzE0M2I1YWY1OGU4ZGRlNjg1NmFiYi5qcGciLCJwb3N0SWQiOiIxNTAyNDQ3MDc0MDIzODEzMTIyIiwidXNlcklkIjoiMTQ5MTk1ODM3NTQxMDQ1MDQzNCIsImNsaWVudF9pZCI6InNhYmVyIiwib3JnYW5pemF0aW9uSWQiOiIxNjUwNzA5NDY4NjgzNDU2NTE0IiwicGhvbmUiOiIxODMyMjA0OTEwOCIsImlkZW50aXR5SWQiOiIxNjUwNzA5NzcwMzMzNjA1ODg5IiwiaWRlbnRpdHlMaXN0IjpbeyJpZCI6IjE2NTA3MDk3NzAzMzM2MDU4ODkiLCJzaXplIjowLCJjdXJyZW50IjowLCJpZHMiOm51bGwsInNlbGVjdElkcyI6bnVsbCwiZGVsZXRlSWRzIjpudWxsLCJzb3J0RmllbGQiOm51bGwsInNvcnRPcmRlciI6bnVsbCwiYXV0byI6dHJ1ZSwidXNlcklkIjoiMTQ5MTk1ODM3NTQxMDQ1MDQzNCIsInBvc3RJZCI6IjE1MDI0NDcwNzQwMjM4MTMxMjIiLCJyb2xlSWQiOiIxNDkxOTU4MTgzMTM1MTY2NDY2IiwiZGVwdElkIjoiMTY1MDcwOTU2OTI2Mjg2NjQzMyIsIm9yZ2FuaXphdGlvbklkIjoiMTY1MDcwOTQ2ODY4MzQ1NjUxNCIsImlzRGVsZXRlZCI6bnVsbCwicm9sZU5hbWUiOiLns7vnu5_nrqHnkIblkZgiLCJwb3N0TmFtZSI6IuaZrumAmuWRmOW3pSIsImRlcHROYW1lIjoi6YOo6ZeoQSIsIm9yZ2FuaXphdGlvbk5hbWUiOiJSYWRpc2jlhazlj7giLCJyb2xlRW50aXR5IjpudWxsfV0sInNjb3BlIjpbImFsbCJdLCJwb3N0TmFtZSI6IuaZrumAmuWRmOW3pSIsIm5pY2tuYW1lIjoi57O757uf566h55CG5ZGYIiwicm9sZU5hbWUiOiLns7vnu5_nrqHnkIblkZgiLCJleHAiOjE3MjE2MTI2MjQsImp0aSI6IjkzOWIyYTU4LWFlZjctNDM1Yy1iMzVjLTE4ZjNmZDk3MTYyNCIsImlzU3lzIjpmYWxzZSwidXNlcm5hbWUiOiJhZG1pbiJ9.beQkwBQTJWh8Z4P6uX_VJuWc5KaMj4OHjxleLYleCjk");
        System.out.println(ObjectUtil.toJSONString(claims));
        System.out.println(claims.getId());
        System.out.println(claims.getExpiration());
        claims.remove("jti");
        claims.remove("exp");
        System.out.println(createJWT(claims.getId(), claims, claims.getExpiration()));
    }
}
