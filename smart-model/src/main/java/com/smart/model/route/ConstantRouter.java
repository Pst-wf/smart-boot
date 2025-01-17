package com.smart.model.route;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 静态路由
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/7/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConstantRouter extends Router {
    private Boolean props;
}
