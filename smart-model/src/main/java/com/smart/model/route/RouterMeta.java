package com.smart.model.route;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

/**
 * 路由Meta
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/6/16
 */
@Data
public class RouterMeta {
    /**
     * 路由的标题
     */
    private String title;
    /**
     * I18n路由-键
     */
    private String i18nKey;
    /**
     * 路由的作用
     * 如果当前用户至少具有其中一个角色，则可以访问路由
     * 它只在路由模式为“静态”时工作，如果路由模式为”动态“，它将被忽略
     */
    private String[] roles;
    /**
     * 是否缓存路由
     */
    private Boolean keepAlive;
    /**
     * 是常量路由
     * 当设置为true时，将不会进行登录验证，也不会进行访问路由的权限验证
     */
    private Boolean constant;
    /**
     * 标志性图标
     * 它可以在菜单或面包屑中使用
     */
    private String icon;
    /**
     * 本地图标
     * 在“src/assets/svg icon”中，如果设置了该图标，则该图标将被忽略
     */
    private String localIcon;
    /**
     * Ant Design 图标
     */
    private String antIcon;
    /**
     * 路由顺序
     */
    private Integer order;
    /**
     * 路由的外部链接
     */
    private String href;
    /**
     * 是否在菜单中隐藏路由
     */
    private Boolean hideInMenu;
    /**
     * 进入路由时，菜单键将被激活路由不在菜单中
     *
     * @apiNote 路由为“user_detail”，如果设置为“user_list”，则菜单“user-list”将被激活
     */
    private String activeMenu;
    /**
     * 默认情况下，相同的路由路径将使用一个选项卡，即使使用不同的查询，如果设置为true，则具有 不同的查询将使用不同的选项卡
     */
    private Boolean multiTab;
    /**
     * 如果设置，则路由将固定在选项卡中，并且该值是固定选项卡的顺序
     */
    private Integer fixedIndexInTab;
    /**
     * 如果设置了查询参数，则进入路由时会自动携带
     *
     * @apiNote {key:string, value:string}
     */
    private JSONArray query;
    /**
     * 按钮
     */
    private List<Button> buttons;
}
