package com.smart.auth.extend.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.smart.auth.extend.wechat.constant.WechatMiniAuthConstant;
import com.smart.common.utils.HttpUtil;
import com.smart.common.utils.ObjectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.model.exception.SmartException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信授权服务
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/8/12
 */
@Slf4j
@Service
public class WechatAuthService {

    /**
     * 获取微信OpenId
     *
     * @param appid  appId
     * @param secret secret
     * @param code   code
     */
    public JSONObject wechatMiniLogin(String appid, String secret, String code) {
        JSONObject params = new JSONObject(true);
        params.put("grant_type", "authorization_code");
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("js_code", code);
        // 超时时间 7200秒
        String result = HttpUtil.get(WechatMiniAuthConstant.LOGIN_URL, ObjectUtil.toJSONString(params));
        if (StringUtil.isBlank(result)) {
            throw new SmartException("小程序登录请求失败！");
        }
        log.info("小程序登录 -> 通过code【{}】-> result ====> {}", code, result);
//        {"session_key":"wRVi+E3zzQX6d8wWpduHWg==","expires_in":7200,"openid":"oUqId0YEhblMcrt1DILI_IaH5cfI","unionid":"oso7T1eSGUHnGp5ZbJk7s7ymC3sg"}
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.containsKey("errcode")) {
            throw new SmartException(jsonObject.getString("errmsg"));
        }
        return jsonObject;
    }

    /**
     * 获取openId和 sessionKey
     *
     * @param code code
     * @return JSONObject
     */
    public JSONObject getOpenIdAndSessionKey(String appid, String secret, String code) {
        if (StringUtil.isBlank(code)) {
            throw new SmartException("code为空！");
        }
        JSONObject jsonObject = wechatMiniLogin(appid, secret, code);
        String sessionKey = jsonObject.getString("session_key");
        String openId = jsonObject.getString("openid");
        log.info("小程序 -> 通过code【{}】 -> session_key ====> {}", code, sessionKey);
        log.info("小程序 -> 通过code【{}】 -> openId ====> {}", code, openId);
        return jsonObject;
    }
}
