package com.smart.model.route;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单路由
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/6/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuRouter extends UserRouter {
    private String id;
    private String parentId;
}
