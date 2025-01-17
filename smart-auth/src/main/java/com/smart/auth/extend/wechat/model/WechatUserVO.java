package com.smart.auth.extend.wechat.model;

import lombok.Data;

/**
 * WechatUserVO
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/8/12
 */
@Data
public class WechatUserVO {
    public String session_key;
    public String unionid;
    public Long openid;
}
