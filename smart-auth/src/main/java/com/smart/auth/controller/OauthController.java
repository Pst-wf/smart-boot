package com.smart.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.smart.auth.captcha.CaptchaGenerator;
import com.smart.auth.extend.wechat.service.WechatAuthService;
import com.smart.auth.service.SmartTokenService;
import com.smart.auth.service.SmartUserDetailsService;
import com.smart.common.constant.SmartConstant;
import com.smart.common.constant.TokenConstant;
import com.smart.common.utils.*;
import com.smart.entity.system.FrontUserEntity;
import com.smart.entity.system.IdentityEntity;
import com.smart.entity.system.LoginLogEntity;
import com.smart.entity.system.UserEntity;
import com.smart.model.auth.LoginAuthRequest;
import com.smart.model.exception.SmartException;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

import static com.smart.common.constant.AuthConstant.*;
import static com.smart.common.constant.SmartConstant.SYSTEM_ID;

/**
 * 用户认证
 *
 * @author wf
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
public class OauthController {
    TokenEndpoint tokenEndpoint;
    LoginLogService loginLogService;
    RoleService roleService;
    UserService userService;
    FrontUserService frontUserService;
    SmartTokenService smartTokenService;
    WechatAuthService wechatAuthService;
    SmartUserDetailsService smartUserDetailsService;
    ConfigService configService;
    RedisUtil redisUtil;

    /**
     * 登录
     *
     * @param principal        principal
     * @param loginAuthRequest 登录请求参数
     * @param request          请求
     * @return java.lang.String
     */
    @PostMapping("/token")
    public String token(Principal principal, @RequestBody LoginAuthRequest loginAuthRequest, HttpServletRequest request) {
        try {
            String grantType = loginAuthRequest.getGrant_type();
            String userType = loginAuthRequest.getUserType();
            String captchaUuid = loginAuthRequest.getCaptchaUuid();
            String captchaCode = loginAuthRequest.getCaptchaCode();

            if (StringUtil.isBlank(grantType)) {
                return Result.fail("登录方式不能为空！");
            }
            if (StringUtil.isBlank(userType)) {
                return Result.fail("用户类型不能为空！");
            }
            // 验证码
            String captchaAuth = configService.getConfig("captcha_auth");
            // 默认只有PC端需要验证码
            if (StringUtil.notBlankAndEquals(captchaAuth, SmartConstant.YES) && userType.equals(USER_TYPE_PC)) {
                if (StringUtil.isBlank(captchaUuid)) {
                    return Result.fail("验证码获取异常！");
                }
                if (StringUtil.isBlank(captchaCode)) {
                    return Result.fail("验证码不能为空！");
                }
                Object o = redisUtil.get(captchaUuid);
                if (o == null) {
                    return Result.fail("验证码已失效！");
                }
                if (!captchaCode.equalsIgnoreCase(o.toString())) {
                    return Result.fail("验证码错误！");
                }
            }
            // 微信登录
            if (grantType.equals("wechat_code")) {
                Map<String, String> configs = configService.getConfigByKeys("wechat_app_id,wechat_secret");
                String appid = configs.get("wechat_app_id");
                String secret = configs.get("wechat_secret");
                if (StringUtil.isBlank(appid) || StringUtil.isBlank(secret)) {
                    return Result.fail("获取微信配置信息异常！");
                }
                JSONObject res = wechatAuthService.getOpenIdAndSessionKey(appid, secret, loginAuthRequest.getWechatCode());
                loginAuthRequest.setOpenId(res.getString("openid"));
                loginAuthRequest.setSessionKey(res.getString("session_key"));
                loginAuthRequest.setUnionId(res.getString("unionid"));
            } else {
                if (StringUtil.isNotBlank(loginAuthRequest.getPassword())) {
                    loginAuthRequest.setPassword(DigestUtil.md5Hex(loginAuthRequest.getPassword()));
                }
            }
            if (StringUtil.isNotBlank(loginAuthRequest.getUsername())) {
                //验证账号是否被锁定（登陆失败导致锁定）
                String msg = smartUserDetailsService.checkAccountLock(loginAuthRequest.getUsername());
                if (StringUtil.isNotBlank(msg)) {
                    return Result.fail(408, msg);
                }
            }
            OAuth2AccessToken body = tokenEndpoint.postAccessToken(principal, MapUtil.toMap(loginAuthRequest)).getBody();
            if (body != null) {
                JSONObject jsonObject = new JSONObject(true);
                String bodyUserType = (String) body.getAdditionalInformation().get(TokenConstant.USER_TYPE);
                if (StringUtil.notBlankAndEquals(bodyUserType, USER_TYPE_PC)) {
                    pcLoginSuccess(jsonObject, body, loginAuthRequest, request);
                } else if (StringUtil.notBlankAndEquals(bodyUserType, USER_TYPE_APP)) {
                    appLoginSuccess(jsonObject, body, loginAuthRequest, request);
                } else {
                    return Result.fail("用户类型未知");
                }
                System.err.println("token -> " + jsonObject.get(TokenConstant.JTI));
                System.err.println("token超时时间 -> " + jsonObject.get(TokenConstant.EXPIRES_TIME));
                if (StringUtil.isNotBlank(loginAuthRequest.getUsername())) {
                    // 清除登录失败的key
                    String key = "lock:" + loginAuthRequest.getUsername();
                    redisUtil.del(key);
                }
                return Result.data(jsonObject);
            } else {
                return Result.fail("登录失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InvalidTokenException) {
                return Result.fail(ResultCode.REFRESH_TOKEN_OVER_TIME);
            }
            if (e instanceof InvalidGrantException) {
                return Result.fail(ResultCode.USER_NOT_FOUND);
            }
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取PC端用户信息
     *
     * @param request 请求
     * @return java.lang.String
     */
    @GetMapping("/getUserInfo")
    public String getUserInfo(HttpServletRequest request) {
        UserEntity user = userService.getUserWithIdentity(AuthUtil.getUserId());
        if (user == null) {
            throw new SmartException(ResultCode.USER_NOT_FOUND);
        }
        List<IdentityEntity> identityList;
        if (user.getId().equals(SYSTEM_ID)) {
            IdentityEntity identity = new IdentityEntity();
            identity.setId(SYSTEM_ID);
            identity.setRoleId(SYSTEM_ID);
            identity.setUserId(SYSTEM_ID);
            identity.setPostName("无");
            identity.setRoleName("超级管理员");
            identity.setOrganizationName("无");
            identity.setDeptName("无");
            identityList = ListUtil.newArrayList(identity);
        } else {
            identityList = user.getIdentityList();
        }
        String identityId = StringUtil.isBlank(request.getHeader(IDENTITY_ID)) ? AuthUtil.getIdentityId() : request.getHeader(IDENTITY_ID);

        IdentityEntity identity = identityList.stream().filter(item -> item.getId().equals(identityId)).findFirst().orElse(null);

        if (identity == null) {
            return Result.fail(ResultCode.UN_CHOOSE_IDENTITY);
        }
        String newToken = smartTokenService.dynamicallyUpdateToken(request, user, identity);
        if (StringUtil.isNotBlank(newToken)) {
            JSONObject jsonObject = new JSONObject(true);
            jsonObject.put(TokenConstant.ACCESS_TOKEN, newToken);
            jsonObject.put(TokenConstant.USER_ID, user.getId());
            jsonObject.put(TokenConstant.IDENTITY_LIST, user.getIdentityList());
            jsonObject.put(TokenConstant.IDENTITY_ID, identity.getId());
            jsonObject.put(TokenConstant.USERNAME, user.getUsername());
            jsonObject.put(TokenConstant.NICKNAME, user.getNickname());
            jsonObject.put(TokenConstant.PHONE, user.getPhone());
            jsonObject.put(TokenConstant.AVATAR, user.getAvatar());
            jsonObject.put(TokenConstant.IS_SYS, user.getIsSys());
            jsonObject.put(TokenConstant.ROLE_ID, identity.getRoleId());
            jsonObject.put(TokenConstant.ROLE_NAME, identity.getRoleName());
            jsonObject.put(TokenConstant.POST_ID, identity.getPostId());
            jsonObject.put(TokenConstant.POST_NAME, identity.getPostName());
            jsonObject.put(TokenConstant.DEPT_ID, identity.getDeptId());
            jsonObject.put(TokenConstant.DEPT_NAME, identity.getDeptName());
            jsonObject.put(TokenConstant.ORGANIZATION_ID, identity.getOrganizationId());
            jsonObject.put(TokenConstant.ORGANIZATION_NAME, identity.getOrganizationName());
            jsonObject.put(TokenConstant.REMARKS, user.getRemarks());
            jsonObject.put(TokenConstant.LAST_LOGIN_DATE, DateUtil.formatDate(user.getLastLoginDate(), "yyyy-MM-dd HH:mm:ss"));
            jsonObject.put(TokenConstant.LAST_LOGIN_IP, user.getLastLoginIp());
            return Result.data(jsonObject);
        } else {
            return Result.fail(ResultCode.OVER_TIME);
        }
    }

    /**
     * 获取移动端用户信息
     *
     * @param request 请求
     * @return java.lang.String
     */
    @GetMapping("/getAppUserInfo")
    public String getAppUserInfo(HttpServletRequest request) {
        FrontUserEntity user = frontUserService.get(AuthUtil.getUserId());
        if (user == null) {
            return Result.fail(ResultCode.OVER_TIME);
        }
        String newToken = smartTokenService.dynamicallyUpdateAppToken(request, user);
        if (StringUtil.isNotBlank(newToken)) {
            JSONObject jsonObject = new JSONObject(true);
            jsonObject.put(TokenConstant.ACCESS_TOKEN, newToken);
            jsonObject.put(TokenConstant.USER_ID, user.getId());
            jsonObject.put(TokenConstant.USERNAME, user.getUsername());
            jsonObject.put(TokenConstant.NICKNAME, user.getNickname());
            jsonObject.put(TokenConstant.GENDER, user.getGender());
            jsonObject.put(TokenConstant.PHONE, user.getPhone());
            jsonObject.put(TokenConstant.AVATAR, user.getAvatar());
            jsonObject.put(TokenConstant.LAST_LOGIN_DATE, user.getLastLoginDate());
            jsonObject.put(TokenConstant.LAST_LOGIN_IP, user.getLastLoginIp());
            jsonObject.put("detail", user);
            return Result.data(jsonObject);
        } else {
            return Result.fail(ResultCode.OVER_TIME);
        }
    }

    /**
     * 获取身份集合信息
     *
     * @return java.lang.String
     */
    @GetMapping("/getIdentityList")
    public String getIdentityList() {
        return Result.data(userService.findIdentityList(AuthUtil.getUserId()));
    }

    /**
     * 异常接口
     *
     * @param code 编码
     * @param msg  消息
     * @return java.lang.String
     */
    @GetMapping("/error")
    public String error(@RequestParam("code") Integer code, @RequestParam("msg") String msg) {
        return Result.fail(code, msg);
    }

    /**
     * 获取验证码
     *
     * @return java.lang.String
     */
    @GetMapping("/captcha")
    public String getCaptcha() {
        int expire = 30;
        Map<String, Object> res = new HashMap<>(0);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        res.put("uuid", uuid);
        Map<String, String> map = CaptchaGenerator.createCaptcha();
        String captcha = map.get("captcha");
        String image = map.get("image");
        res.put("image", image);
        res.put("expire", expire);
        // 30秒有效
        redisUtil.set(uuid, captcha, expire);
        return Result.data(res);
    }
//
//
//	@GetMapping("/oauth/clear-cache")
//	public Kv clearCache() {
//		CacheUtil.clear(CacheConstant.BIZ_CACHE);
//		CacheUtil.clear(CacheConstant.MENU_CACHE);
//		CacheUtil.clear(CacheConstant.USER_CACHE);
//		CacheUtil.clear(CacheConstant.DICT_CACHE);
//		CacheUtil.clear(CacheConstant.FLOW_CACHE);
//		CacheUtil.clear(CacheConstant.SYS_CACHE);
//		CacheUtil.clear(CacheConstant.RESOURCE_CACHE);
//		CacheUtil.clear(CacheConstant.PARAM_CACHE);
//		return Kv.create().set("success", "true").set("msg", "success");
//	}

    /**
     * 后台用户登录成功
     *
     * @param jsonObject       返回JSON
     * @param body             登陆成功后的token
     * @param loginAuthRequest 请求参数
     * @param request          请求
     */
    private void pcLoginSuccess(JSONObject jsonObject, OAuth2AccessToken body, LoginAuthRequest loginAuthRequest, HttpServletRequest request) {
        String ipAddress = IPUtil.getIpAddress(request);
        Date date = new Date();
        String dateStr = DateUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        jsonObject.put(TokenConstant.ACCESS_TOKEN, body.getValue());
        jsonObject.put(TokenConstant.REFRESH_TOKEN, body.getRefreshToken().getValue());
        jsonObject.put(TokenConstant.TOKEN_TYPE, body.getTokenType());
        jsonObject.put(TokenConstant.EXPIRES_IN, body.getExpiresIn());
        jsonObject.put(TokenConstant.EXPIRES_TIME, DateUtil.formatDate(System.currentTimeMillis() + body.getExpiresIn() * 1000L, "yyyy-MM-dd HH:mm:ss"));
        jsonObject.put(TokenConstant.SCOPE, String.join(",", body.getScope()));
        // "jti": "jwt的唯一身份标识，用来作为一次性token时可以防止重放攻击",
        jsonObject.put(TokenConstant.JTI, body.getAdditionalInformation().get(TokenConstant.JTI));
        jsonObject.put(TokenConstant.USER_ID, body.getAdditionalInformation().get(TokenConstant.USER_ID));
        jsonObject.put(TokenConstant.IDENTITY_LIST, body.getAdditionalInformation().get(TokenConstant.IDENTITY_LIST));
        jsonObject.put(TokenConstant.IDENTITY_ID, body.getAdditionalInformation().get(TokenConstant.IDENTITY_ID));
        jsonObject.put(TokenConstant.USERNAME, body.getAdditionalInformation().get(TokenConstant.USERNAME));
        jsonObject.put(TokenConstant.NICKNAME, body.getAdditionalInformation().get(TokenConstant.NICKNAME));
        jsonObject.put(TokenConstant.GENDER, body.getAdditionalInformation().get(TokenConstant.GENDER));
        jsonObject.put(TokenConstant.PHONE, body.getAdditionalInformation().get(TokenConstant.PHONE));
        jsonObject.put(TokenConstant.EMAIL, body.getAdditionalInformation().get(TokenConstant.EMAIL));
        jsonObject.put(TokenConstant.AVATAR, body.getAdditionalInformation().get(TokenConstant.AVATAR));
        jsonObject.put(TokenConstant.IS_SYS, body.getAdditionalInformation().get(TokenConstant.IS_SYS));
        jsonObject.put(TokenConstant.ROLE_ID, body.getAdditionalInformation().get(TokenConstant.ROLE_ID));
        jsonObject.put(TokenConstant.ROLE_NAME, body.getAdditionalInformation().get(TokenConstant.ROLE_NAME));
        jsonObject.put(TokenConstant.POST_ID, body.getAdditionalInformation().get(TokenConstant.POST_ID));
        jsonObject.put(TokenConstant.POST_NAME, body.getAdditionalInformation().get(TokenConstant.POST_NAME));
        jsonObject.put(TokenConstant.DEPT_ID, body.getAdditionalInformation().get(TokenConstant.DEPT_ID));
        jsonObject.put(TokenConstant.DEPT_NAME, body.getAdditionalInformation().get(TokenConstant.DEPT_NAME));
        jsonObject.put(TokenConstant.ORGANIZATION_ID, body.getAdditionalInformation().get(TokenConstant.ORGANIZATION_ID));
        jsonObject.put(TokenConstant.ORGANIZATION_NAME, body.getAdditionalInformation().get(TokenConstant.ORGANIZATION_NAME));
        jsonObject.put(TokenConstant.REMARKS, body.getAdditionalInformation().get(TokenConstant.REMARKS));
        jsonObject.put(TokenConstant.LAST_LOGIN_DATE, dateStr);
        jsonObject.put(TokenConstant.LAST_LOGIN_IP, ipAddress);
        if (jsonObject.getString(TokenConstant.IDENTITY_ID) != null) {
            // 记录登录日志
            LoginLogEntity loginLogEntity = new LoginLogEntity();
            loginLogEntity.setUserId(jsonObject.getString(TokenConstant.USER_ID));
            loginLogEntity.setUserNickname(jsonObject.getString(TokenConstant.NICKNAME));
            loginLogEntity.setUsername(jsonObject.getString(TokenConstant.USERNAME));
            loginLogEntity.setGrantType(loginAuthRequest.getGrant_type());
            loginLogEntity.setIp(ipAddress);
            loginLogService.saveEntity(loginLogEntity);
            // 更新用户登录信息
            UserEntity user = new UserEntity();
            user.setId(jsonObject.getString(TokenConstant.USER_ID));
            user.setLastLoginDate(date);
            user.setLastLoginIp(loginLogEntity.getIp());
            userService.updateLoginInfo(user);
            // 登录成功数据权限存入缓存
            roleService.getScopes(jsonObject.getString(TokenConstant.ROLE_ID));
            // 登录成功按钮权限存入缓存
            roleService.getButtons(jsonObject.getString(TokenConstant.ROLE_ID));
        }
    }

    /**
     * 前台用户登录成功
     *
     * @param jsonObject       返回JSON
     * @param body             登陆成功后的token
     * @param loginAuthRequest 请求参数
     * @param request          请求
     */
    private void appLoginSuccess(JSONObject jsonObject, OAuth2AccessToken body, LoginAuthRequest loginAuthRequest, HttpServletRequest request) {
        String ipAddress = IPUtil.getIpAddress(request);
        Date date = new Date();
        String dateStr = DateUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        jsonObject.put(TokenConstant.ACCESS_TOKEN, body.getValue());
        jsonObject.put(TokenConstant.REFRESH_TOKEN, body.getRefreshToken().getValue());
        jsonObject.put(TokenConstant.TOKEN_TYPE, body.getTokenType());
        jsonObject.put(TokenConstant.EXPIRES_IN, body.getExpiresIn());
        jsonObject.put(TokenConstant.EXPIRES_TIME, DateUtil.formatDate(System.currentTimeMillis() + body.getExpiresIn() * 1000L, "yyyy-MM-dd HH:mm:ss"));
        jsonObject.put(TokenConstant.SCOPE, String.join(",", body.getScope()));
        // "jti": "jwt的唯一身份标识，用来作为一次性token时可以防止重放攻击",
        jsonObject.put(TokenConstant.JTI, body.getAdditionalInformation().get(TokenConstant.JTI));
        jsonObject.put(TokenConstant.USER_ID, body.getAdditionalInformation().get(TokenConstant.USER_ID));
        jsonObject.put(TokenConstant.USERNAME, body.getAdditionalInformation().get(TokenConstant.USERNAME));
        jsonObject.put(TokenConstant.NICKNAME, body.getAdditionalInformation().get(TokenConstant.NICKNAME));
        jsonObject.put(TokenConstant.GENDER, body.getAdditionalInformation().get(TokenConstant.GENDER));
        jsonObject.put(TokenConstant.PHONE, body.getAdditionalInformation().get(TokenConstant.PHONE));
        jsonObject.put(TokenConstant.AVATAR, body.getAdditionalInformation().get(TokenConstant.AVATAR));
        jsonObject.put(TokenConstant.LAST_LOGIN_DATE, dateStr);
        jsonObject.put(TokenConstant.LAST_LOGIN_IP, ipAddress);
        // 更新用户登录信息
        FrontUserEntity user = new FrontUserEntity();
        user.setId(jsonObject.getString(TokenConstant.USER_ID));
        user.setLastLoginDate(date);
        user.setLastLoginIp(ipAddress);
        frontUserService.updateLoginInfo(user);
        // TODO 前待登录日志
//                // 记录登录日志
//                LoginLogEntity loginLogEntity = new LoginLogEntity();
//                loginLogEntity.setUserId(jsonObject.getString(TokenConstant.USER_ID));
//                loginLogEntity.setUserNickname(jsonObject.getString(TokenConstant.NICKNAME));
//                loginLogEntity.setUsername(jsonObject.getString(TokenConstant.USERNAME));
//                loginLogEntity.setGrantType(loginAuthRequest.getGrant_type());
//                loginLogEntity.setIp(IPUtil.getIpAddress(request));
//                loginLogService.saveEntity(loginLogEntity);
    }
}
