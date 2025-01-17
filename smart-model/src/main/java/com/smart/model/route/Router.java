package com.smart.model.route;

import lombok.Data;

import java.util.List;

/**
 * 路由
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/6/16
 */
@Data
public class Router {

    private String name;
    private String path;
    private String redirect;
    private String component;
    private RouterMeta meta;
    private List<Router> children;

}
