package com.smart.model.route;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户路由
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/7/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRouter extends Router {
    private JSONObject props;
}
