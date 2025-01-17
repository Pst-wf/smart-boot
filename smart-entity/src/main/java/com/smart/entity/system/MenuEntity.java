package com.smart.entity.system;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import com.smart.mybatis.handler.FastJSONArrayTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 菜单表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_menu")
public class MenuEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 菜单类型
     */
    @Column(queryType = QueryType.EQ)
    private String menuType;

    /**
     * 菜单名称
     */
    @Column(queryType = QueryType.LIKE)
    private String menuName;

    /**
     * 路由名称
     */
    private String routeName;

    /**
     * 路由路径
     */
    private String routePath;

    /**
     * 路径参数
     */
    private String pathParam;

    /**
     * 组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标类型
     */
    private String iconType;

    /**
     * Ant Design 图标
     */
    private String antIcon;
    /**
     * 国际化key
     */
    private String i18nKey;

    /**
     * 缓存路由
     */
    private Boolean keepAlive;

    /**
     * 常量路由
     */
    private Boolean constant;

    /**
     * 排序
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 外链
     */
    private String href;

    /**
     * 重定向
     */
    private String redirect;

    /**
     * 隐藏菜单
     */
    private Boolean hideInMenu;

    /**
     * 高亮的菜单
     */
    private String activeMenu;

    /**
     * 支持多页签
     */
    private Boolean multiTab;

    /**
     * 固定在页签中的序号
     */
    private Integer fixedIndexInTab;

    /**
     * 路由参数
     */
    @TableField(typeHandler = FastJSONArrayTypeHandler.class)
    private JSONArray query;

    /**
     * 布局
     */
    private String layout;

    /**
     * props参数
     */
    private String props;

    /**
     * 权重
     */
    @Column(queryType = QueryType.EQ)
    private String weight;

    /**
     * 启用状态
     */
    @Column(queryType = QueryType.EQ)
    private String status;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<MenuEntity> children;

    /**
     * 按钮集合
     */
    @TableField(exist = false)
    private List<ButtonsEntity> buttons;

    /**
     * 数据权限
     */
    @TableField(exist = false)
    private List<ScopeEntity> scopeList;

}
