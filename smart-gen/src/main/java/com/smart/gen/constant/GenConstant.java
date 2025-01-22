package com.smart.gen.constant;

/**
 * 代码生成器常量
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/5/5
 */
public class GenConstant {
    /**
     * 基础默认字段
     */
    public static final String[] DEFAULT_COLUMNS = {"id", "is_deleted", "create_by", "create_user", "create_dept", "create_organization", "create_date", "update_by", "update_date", "remarks"};

    /**
     * Entity模板名称
     */
    public static final String ENTITY_JAVA = "Entity.java.vm";
    /**
     * Dao模板名称
     */
    public static final String DAO_JAVA = "Dao.java.vm";
    /**
     * Service模板名称
     */
    public static final String SERVICE_JAVA = "Service.java.vm";
    /**
     * ServiceImpl模板名称
     */
    public static final String SERVICE_IMPL_JAVA = "ServiceImpl.java.vm";
    /**
     * Controller模板名称
     */
    public static final String CONTROLLER_JAVA = "Controller.java.vm";
    /**
     * Dao.xml模板名称
     */
    public static final String DAO_XML = "Dao.xml.vm";
    /* *************************** Ant Design Vue2 *************************** */
    /**
     * list.vue模板名称
     */
    public static final String LIST_VUE = "list.vue.vm";
    /**
     * form.vue模板名称
     */
    public static final String FORM_VUE = "form.vue.vm";
    /**
     * api.js模板名称
     */
    public static final String API_JS = "api.js.vm";

    /* *************************** Naive Vue *************************** */
    /**
     * index.ts模板名称
     */
    public static final String SERVICE_TS = "index.ts.vm";

    /**
     * index.d.ts模板名称
     */
    public static final String TYPINGS_TS = "index.d.ts.vm";
    /**
     * index.vue模板名称
     */
    public static final String VIEWS_VUE = "index.vue.vm";
    /**
     * operate-modal.vue模板名称
     */
    public static final String VIEWS_OPERATE_MODAL = "operate-modal.vue.vm";
    /**
     * operate-modal.vue模板名称
     */
    public static final String VIEWS_OPERATE_DRAWER = "operate-drawer.vue.vm";
    /**
     * search.vue模板名称
     */
    public static final String VIEWS_SEARCH = "search.vue.vm";
    /**
     * app.d.ts模板名称
     */
    public static final String SETTINGS_APP = "app.d.ts.vm";
    /**
     * zh-cn.ts模板名称
     */
    public static final String SETTINGS_ZH = "zh-cn.ts.vm";
    /**
     * en-us.ts模板名称
     */
    public static final String SETTINGS_EN = "en-us.ts.vm";

    /* *************************** Ant Design Vue3 *************************** */
    public static final String ANT_MARK = "ant_design";
    public static final String ANT_API = "api.js.vm";
    public static final String ANT_COLUMNS = "columns.js.vm";
    public static final String ANT_VIEWS_VUE = "index.vue.vm";
    public static final String ANT_VIEWS_OPERATE_MODAL = "operate-modal.vue.vm";
    public static final String ANT_VIEWS_OPERATE_DRAWER = "operate-drawer.vue.vm";

    /**
     * 类型
     */
    public static final String KEY_OPTION_GEN_TYPE = "genType";
    /**
     * 全部
     */
    public static final String OPTION_GEN_TYPE_1 = "1";
    /**
     * api
     */
    public static final String OPTION_GEN_TYPE_2 = "2";
    /**
     * 接口
     */
    public static final String OPTION_GEN_TYPE_3 = "3";
    /**
     * 表单类型
     */
    public static final String KEY_OPTION_FORM_TYPE = "formType";

    /**
     * api
     */
    public static final String OPTION_FORM_TYPE_MODAL = "modal";
    /**
     * 接口
     */
    public static final String OPTION_FORM_TYPE_DRAWER = "drawer";
}
