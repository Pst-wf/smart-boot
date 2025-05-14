package com.smart.gen.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.ObjectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.gen.GenTableColumnEntity;
import com.smart.entity.gen.GenTableEntity;
import com.smart.model.exception.SmartException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.smart.gen.constant.GenConstant.*;

/**
 * 代码生成器   工具类
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public class GenUtils {
    /**
     * 数字类型
     */
    private static final String[] NUMBER_TYPES = {"Integer", "Long", "Float", "Double", "BigDecimal"};

    /**
     * 获取模板
     *
     * @param option    获取模板参数 （1 全部; 2 API; 3 接口）
     * @param frontType 前端类型 不传则包含全部
     * @return List
     */
    public static List<String> getTemplates(JSONObject option, String frontType) {
        String genType = option.getString(KEY_OPTION_GEN_TYPE);
        String formType = option.getString(KEY_OPTION_FORM_TYPE);
        List<String> templates = new ArrayList<>();
        if (StringUtil.notBlankAndEquals(genType, OPTION_GEN_TYPE_3)) {
            // 接口
            templates.add("template/java/entity/Entity.java.vm");
            templates.add("template/java/dao/Dao.xml.vm");
            templates.add("template/java/service/Service.java.vm");
            templates.add("template/java/service/ServiceImpl.java.vm");
            templates.add("template/java/dao/Dao.java.vm");
            templates.add("template/java/controller/Controller.java.vm");
        } else if (StringUtil.notBlankAndEquals(genType, OPTION_GEN_TYPE_2)) {
            // API
            templates.add("template/java/entity/Entity.java.vm");
            templates.add("template/java/dao/Dao.xml.vm");
            templates.add("template/java/service/Service.java.vm");
            templates.add("template/java/service/ServiceImpl.java.vm");
            templates.add("template/java/dao/Dao.java.vm");
        } else {
            // 全部
            templates.add("template/java/entity/Entity.java.vm");
            templates.add("template/java/dao/Dao.xml.vm");
            templates.add("template/java/service/Service.java.vm");
            templates.add("template/java/service/ServiceImpl.java.vm");
            templates.add("template/java/dao/Dao.java.vm");
            templates.add("template/java/controller/Controller.java.vm");
            // v2
//            templates.add("template/vue/v2/views/list.vue.vm");
//            templates.add("template/vue/v2/views/form.vue.vm");
//            templates.add("template/vue/v2/api/api.js.vm");
            if (StringUtil.notBlankAndEquals(frontType, NAIVE_MARK) || StringUtil.isBlank(frontType)) {
                // Naive vue
                templates.add("template/vue/v3/naive/typings/index.d.ts.vm");
                templates.add("template/vue/v3/naive/service/index.ts.vm");
                templates.add("template/vue/v3/naive/views/index.vue.vm");
                if (StringUtil.notBlankAndEquals(formType, OPTION_FORM_TYPE_DRAWER)) {
                    templates.add("template/vue/v3/naive/views/modules/operate-drawer.vue.vm");
                } else {
                    templates.add("template/vue/v3/naive/views/modules/operate-modal.vue.vm");
                }
                templates.add("template/vue/v3/naive/views/modules/search.vue.vm");
                templates.add("template/vue/v3/naive/settings/i18n/app.d.ts.vm");
                templates.add("template/vue/v3/naive/settings/i18n/en-us.ts.vm");
                templates.add("template/vue/v3/naive/settings/i18n/zh-cn.ts.vm");
            }
            if (StringUtil.notBlankAndEquals(frontType, ANT_MARK) || StringUtil.isBlank(frontType)) {
                // Ant Design vue3
                templates.add("template/vue/v3/ant_design/views/index.vue.vm");
                if (StringUtil.notBlankAndEquals(formType, OPTION_FORM_TYPE_DRAWER)) {
                    templates.add("template/vue/v3/ant_design/views/components/operate-drawer.vue.vm");
                } else {
                    templates.add("template/vue/v3/ant_design/views/components/operate-modal.vue.vm");
                }
                templates.add("template/vue/v3/ant_design/views/columns.js.vm");
                templates.add("template/vue/v3/ant_design/api/api.js.vm");
            }
        }
        return templates;
    }

    /**
     * 生成代码
     *
     * @param table   表bean
     * @param columns 字段List
     * @param zip     zip流
     */
    public static void generatorCode(GenTableEntity table, List<GenTableColumnEntity> columns, ZipOutputStream zip) {
        VelocityContext context = initVelocityContext(table, columns);
        //获取模板列表
        List<String> templates = getTemplates(table.getOptions(), table.getFrontType());
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                //添加到zip
                String fileName = getFileName(template, table, false);
                if (StringUtil.isNotBlank(fileName)) {
                    zip.putNextEntry(new ZipEntry(fileName));
                    IOUtils.write(sw.toString(), zip, "UTF-8");
                    IOUtils.closeQuietly(sw);
                    zip.closeEntry();
                }
            } catch (IOException e) {
                throw new SmartException("渲染模板失败，表名：" + table.getTableName());
            }
        }
    }

    /**
     * 生成代码到指定目录
     *
     * @param table   表bean
     * @param columns 字段List
     */
    public static void generatorCodeInFile(GenTableEntity table, List<GenTableColumnEntity> columns) {
        VelocityContext context = initVelocityContext(table, columns);
        //获取模板列表
        List<String> templates = getTemplates(table.getOptions(), table.getFrontType());
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            String fileName = getFileName(template, table, true);
            System.out.println(fileName);
            if (StringUtil.isNotBlank(fileName)) {
                System.out.println(fileName);
                File file = new File(fileName);
                File parentFile = file.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    throw new SmartException(parentFile.getAbsolutePath() + "创建出错！");
                }
                try {
                    if (!file.exists() && !file.createNewFile()) {
                        throw new SmartException(fileName + "创建出错！");
                    }
                    if (file.isDirectory()) {
                        if (!file.delete()) {
                            throw new SmartException(fileName + "创建出错！");
                        }
                        if (!file.createNewFile()) {
                            throw new SmartException(fileName + "创建出错！");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (OutputStream outputStream = Files.newOutputStream(Paths.get(fileName))) {
                    // 写入字符串，默认字符集
                    IOUtils.write(sw.toString(), outputStream, "UTF-8");
                    // 别忘了刷新和关闭流
                    outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 预览
     *
     * @param table   表bean
     * @param columns 字段List
     */
    public static List<Map<String, Object>> previewCode(GenTableEntity table, List<GenTableColumnEntity> columns) {
        VelocityContext context = initVelocityContext(table, columns);
        //获取模板列表
        List<String> templates = getTemplates(table.getOptions(), table.getFrontType());

        List<Map<String, Object>> list = getDefault(table.getFrontType());
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            Map<String, Object> dataMap = getMap(template, sw.toString(), table);
            if (dataMap != null) {
                list.add(dataMap);
            }
        }
        return list;
    }

    /**
     * 初始化 Velocity 配置
     *
     * @param table   生成表单
     * @param columns 生成表字段
     * @return VelocityContext
     */
    private static VelocityContext initVelocityContext(GenTableEntity table, List<GenTableColumnEntity> columns) {
        // 配置信息
        boolean hasBigDecimal = false;
        boolean hasDate = false;
        boolean hasQueryDate = false;
        boolean hasFormDate = false;
        boolean hasNotNull = false;
        boolean hasCheckLength = false;
        boolean extentBaseEntity = false;
        boolean hasList = false;
        boolean hasQuery = false;
        // 前端页面控制
        boolean hasCheckBox = false;
        boolean hasCascader = false;
        boolean hasTreeSelect = false;
        boolean hasEditor = false;
        boolean hasListImage = false;
        boolean hasFullRow = false;
        GenTableColumnEntity pk = null;
        boolean isModal = true;
        if (table.getOptions() != null && StringUtil.notBlankAndEquals(table.getOptions().getString(KEY_OPTION_FORM_TYPE), OPTION_FORM_TYPE_DRAWER)) {
            isModal = false;
        }
        boolean isPage = true;
        if (table.getOptions() != null && StringUtil.notBlankAndEquals(table.getOptions().getString(KEY_OPTION_TABLE_TYPE), OPTION_TABLE_TYPE_LIST)) {
            isPage = false;
        }
        // 校验项
        List<GenTableColumnEntity> validateColumns = new ArrayList<>();
        // 不校验的项
        List<GenTableColumnEntity> unValidateColumns = new ArrayList<>();
        // 筛选项
        List<GenTableColumnEntity> queryColumns = new ArrayList<>();
        // 列表项
        List<GenTableColumnEntity> listColumns = new ArrayList<>();
        // 表单项
        List<GenTableColumnEntity> formColumns = new ArrayList<>();
        // 上传项
        List<GenTableColumnEntity> uploadColumns = new ArrayList<>();
        // 图片上传项
        List<GenTableColumnEntity> imageUploadColumns = new ArrayList<>();
        // 区间查询
        List<GenTableColumnEntity> betweenColumns = new ArrayList<>();
        // NotIn查询
        List<GenTableColumnEntity> notInColumns = new ArrayList<>();
        // In查询
        List<GenTableColumnEntity> inColumns = new ArrayList<>();
        // Like_In_And查询
        List<GenTableColumnEntity> likeInAndColumns = new ArrayList<>();
        // Like_In_Or查询
        List<GenTableColumnEntity> likeInOrColumns = new ArrayList<>();
        // 国际化字段集合
        List<GenTableColumnEntity> i18n = new ArrayList<>();
        List<GenTableColumnEntity> i18nForm = new ArrayList<>();
        // 筛选项 列表项 字典字段
        List<String> listDictColumns = new ArrayList<>();
        // 表单项 字典字段
        List<String> formDictColumns = new ArrayList<>();
        // 筛选项 字典字段
        List<String> queryDictColumns = new ArrayList<>();
        // 所有字典code
        Set<String> dictCodes = new HashSet<>();
        // 筛选级联选择器字段
        List<GenTableColumnEntity> queryCascaderColumns = new ArrayList<>();
        // 级联选择器字典字段
        List<GenTableColumnEntity> cascaderDictColumns = new ArrayList<>();
        // 复选框字段
        List<GenTableColumnEntity> checkboxColumns = new ArrayList<>();
        // 筛选复选框字段
        List<GenTableColumnEntity> queryCheckboxColumns = new ArrayList<>();
        // 数字输入框字段
        List<GenTableColumnEntity> inputNumberColumns = new ArrayList<>();
        // 数字输入框字段
        List<GenTableColumnEntity> queryInputNumberColumns = new ArrayList<>();

        for (GenTableColumnEntity columnEntity : columns) {
            columnEntity.setAttrNameBig(StrUtil.upperFirst(columnEntity.getAttrName()));
            columnEntity.setI18nValue(getI18nValue(columnEntity.getColumnName()));
            columnEntity.setPlaceholderValue(getPlaceholderValue(columnEntity.getColumnName()));
            if (Arrays.asList(DEFAULT_COLUMNS).contains(columnEntity.getColumnName())) {
                columnEntity.setIsDefault(true);
                if (!columnEntity.getColumnName().equals("id")) {
                    extentBaseEntity = true;
                }
            } else {
                columnEntity.setIsDefault(false);
            }
            columnEntity.setIsNumber(Arrays.asList(NUMBER_TYPES).contains(columnEntity.getAttrType()));

            if (StringUtil.notBlankAndEquals(columnEntity.getIsPk(), "1")) {
                pk = columnEntity;
            }

            if ("1".equals(columnEntity.getIsForm()) && !Arrays.asList(DEFAULT_COLUMNS).contains(columnEntity.getColumnName())) {
                boolean checkLength = Arrays.asList(MAX_LENGTH_CHECK_COMPONENTS).contains(columnEntity.getComponents()) && (
                        columnEntity.getColumnLength() != null && new BigDecimal(columnEntity.getColumnLength()).compareTo(new BigDecimal(999999)) <= 0);
                if ("1".equals(columnEntity.getIsNotNull()) || checkLength) {
                    if ("1".equals(columnEntity.getIsNotNull())) {
                        hasNotNull = true;
                    }
                    if (checkLength) {
                        hasCheckLength = true;
                        columnEntity.setIsCheckLength(true);
                    }
                    validateColumns.add(columnEntity);
                } else {
                    unValidateColumns.add(columnEntity);
                }
            }

            if (!hasBigDecimal && "BigDecimal".equals(columnEntity.getAttrType())) {
                hasBigDecimal = true;
            }
            if (!hasDate && "Date".equals(columnEntity.getAttrType())) {
                if (!"create_date".equals(columnEntity.getColumnName()) && !"update_date".equals(columnEntity.getColumnName())) {
                    hasDate = true;
                }
            }
            if (!hasQuery && StringUtil.isNotBlank(columnEntity.getQueryType())) {
                hasQuery = true;
            }
            //列表项
            if (StringUtil.notBlankAndEquals(columnEntity.getIsList(), "1")) {
                listColumns.add(columnEntity);
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "IMAGE-UPLOAD")) {
                    hasListImage = true;
                }
            }
            //表单项
            if (StringUtil.notBlankAndEquals(columnEntity.getIsForm(), "1")) {
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "CHECKBOX")) {
                    hasCheckBox = true;
                    checkboxColumns.add(columnEntity);
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "INPUT-NUMBER") && !columnEntity.getIsNumber()) {
                    inputNumberColumns.add(columnEntity);
                }


                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "CASCADER")) {
                    hasCascader = true;
                    if (StringUtil.isNotBlank(columnEntity.getDictCode())) {
                        cascaderDictColumns.add(columnEntity);
                    }
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "TREE-SELECT")) {
                    hasTreeSelect = true;
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getRowStyle(), "1")) {
                    hasFullRow = true;
                }
                if (!hasFormDate && "Date".equals(columnEntity.getAttrType())) {
                    hasFormDate = true;
                }
                formColumns.add(columnEntity);
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "FILE-UPLOAD")) {
                    GenTableColumnEntity copyBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                    String attrName = columnEntity.getAttrName().replace("Ids", "List");
                    if (!attrName.contains("List")) {
                        attrName += "List";
                    }
                    copyBean.setAttrName(attrName);
                    copyBean.setAttrNameBig(StrUtil.upperFirst(copyBean.getAttrName()));
                    if (StringUtil.isNotBlank(columnEntity.getComments())) {
                        String lowerCase = columnEntity.getComments().toLowerCase();
                        String comments = lowerCase.replace("ids", "集合");
                        copyBean.setComments(comments);
                    }
                    copyBean.setRefUpload(columnEntity);
                    uploadColumns.add(copyBean);
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "IMAGE-UPLOAD")) {
                    imageUploadColumns.add(columnEntity);
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "EDITOR")) {
                    hasEditor = true;
                }
            }
            //是否关联字典
            if (StringUtil.isNotBlank(columnEntity.getDictCode())) {
                if (hasQuery || listColumns.contains(columnEntity)) {
                    listDictColumns.add(columnEntity.getDictCode());
                }
                if (formColumns.contains(columnEntity)) {
                    formDictColumns.add(columnEntity.getDictCode());
                }
            }
            //查询项单项
            if (StringUtil.isNotBlank(columnEntity.getQueryType())) {

                if (!hasQueryDate && "Date".equals(columnEntity.getAttrType())) {
                    hasQueryDate = true;
                }
                switch (columnEntity.getQueryType()) {
                    case "BETWEEN":
                        GenTableColumnEntity betweenBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                        if (betweenBean.getAttrType().equals("Date")) {
                            betweenBean.setAttrType("String");
                        }
                        String betweenAttrName = columnEntity.getAttrName() + "BetweenList";
                        String betweenComments = columnEntity.getComments() + "Between查询集合";
                        betweenBean.setAttrName(betweenAttrName);
                        betweenBean.setComments(betweenComments);
                        betweenColumns.add(betweenBean);
                        break;
                    case "NOT_IN":
                        GenTableColumnEntity notInBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                        if (notInBean.getAttrType().equals("Date")) {
                            notInBean.setAttrType("String");
                        }
                        String notInAttrName = columnEntity.getAttrName() + "NotInList";
                        String notInComments = columnEntity.getComments() + "NotIn查询集合";
                        notInBean.setAttrName(notInAttrName);
                        notInBean.setComments(notInComments);
                        notInColumns.add(notInBean);
                        break;
                    case "IN":
                        GenTableColumnEntity inBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                        if (inBean.getAttrType().equals("Date")) {
                            inBean.setAttrType("String");
                        }
                        String inAttrName = columnEntity.getAttrName() + "InList";
                        String inComments = columnEntity.getComments() + "In查询集合";
                        inBean.setAttrName(inAttrName);
                        inBean.setComments(inComments);
                        inColumns.add(inBean);
                        break;
                    case "LIKE_IN_AND":
                        GenTableColumnEntity likeInAndBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                        if (likeInAndBean.getAttrType().equals("Date")) {
                            likeInAndBean.setAttrType("String");
                        }
                        String likeInAndAttrName = columnEntity.getAttrName() + "likeInAndList";
                        String likeInAndComments = columnEntity.getComments() + "likeInAnd查询集合";
                        likeInAndBean.setAttrName(likeInAndAttrName);
                        likeInAndBean.setComments(likeInAndComments);
                        likeInAndColumns.add(likeInAndBean);
                        break;
                    case "LIKE_IN_OR":
                        GenTableColumnEntity likeInOrBean = (GenTableColumnEntity) ObjectUtil.copyBean(columnEntity);
                        if (likeInOrBean.getAttrType().equals("Date")) {
                            likeInOrBean.setAttrType("String");
                        }
                        String likeInOrAttrName = columnEntity.getAttrName() + "likeInOrList";
                        String likeInOrComments = columnEntity.getComments() + "likeInOr查询集合";
                        likeInOrBean.setAttrName(likeInOrAttrName);
                        likeInOrBean.setComments(likeInOrComments);
                        likeInOrColumns.add(likeInOrBean);
                        break;
                    default:
                        queryColumns.add(columnEntity);
                        break;
                }

                if (StringUtil.isNotBlank(columnEntity.getDictCode())) {
                    queryDictColumns.add(columnEntity.getDictCode());
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "CHECKBOX")) {
                    if (!columnEntity.getQueryType().equals("IN") && !columnEntity.getQueryType().equals("MOT_IN")) {
                        queryCheckboxColumns.add(columnEntity);
                    }
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "CASCADER")) {
                    queryCascaderColumns.add(columnEntity);
                }
                if (StringUtil.notBlankAndEquals(columnEntity.getComponents(), "INPUT-NUMBER") && !columnEntity.getIsNumber()) {
                    queryInputNumberColumns.add(columnEntity);
                }
            }
        }
        //如果没有主键,则默认第一个字段为主键
        if (pk == null) {
            if (!columns.isEmpty()) {
                pk = columns.get(0);
            }
        }
        listColumns.forEach(column -> {
            if (column.getColumnName().equals("create_by") || column.getColumnName().equals("create_user")) {
                column.setComments("创建人");
                column.setAttrName("createUserName");
            }
            if (column.getColumnName().equals("create_dept")) {
                column.setComments("创建人部门");
                column.setAttrName("createDeptName");
            }
            if (column.getColumnName().equals("create_organization")) {
                column.setComments("创建人公司");
                column.setAttrName("createOrganizationName");
            }
        });
        // 根据attrName去重
        listColumns = listColumns.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GenTableColumnEntity::getAttrName))), ArrayList::new));
        // 排序
        listColumns = listColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        validateColumns = validateColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        unValidateColumns = unValidateColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        queryColumns = queryColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        formColumns = formColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        uploadColumns = uploadColumns.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());

        i18n.addAll(listColumns);
        i18n.addAll(queryColumns);
        i18nForm.addAll(formColumns);
        i18nForm.addAll(queryColumns);
        // 根据attrName去重
        i18n = i18n.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GenTableColumnEntity::getAttrName))), ArrayList::new));
        i18nForm = i18nForm.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GenTableColumnEntity::getAttrName))), ArrayList::new));
        i18n = i18n.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());
        i18nForm = i18nForm.stream().sorted(Comparator.comparing(GenTableColumnEntity::getColumnSort)).collect(Collectors.toList());


        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = table.getPackageName();
        mainPath = StringUtil.isBlank(mainPath) ? "com.radish" : mainPath;
        //封装模板数据
        Map<String, Object> map = new HashMap<>(0);
        map.put("tableName", table.getTableName());
        map.put("comments", table.getComments());
        map.put("pk", pk);
        map.put("className", table.getClassName());
        map.put("classname", StringUtil.uncapitalize(table.getClassName()));
        map.put("classnameLowerCase", StringUtil.uncapitalize(table.getClassName()).toLowerCase(Locale.ROOT));
        map.put("pathName", StringUtil.uncapitalize(table.getClassName()));
        map.put("columns", columns);
        map.put("hasNotNull", hasNotNull);
        map.put("hasCheckLength", hasCheckLength);
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasDate", hasDate);
        map.put("hasQueryDate", hasQueryDate);
        map.put("hasFormDate", hasFormDate);
        map.put("extentBaseEntity", extentBaseEntity);
        map.put("hasQuery", hasQuery);
        map.put("hasCheckBox", hasCheckBox);
        map.put("hasCascader", hasCascader);
        map.put("hasListImage", hasListImage);
        map.put("hasTreeSelect", hasTreeSelect);
        map.put("hasFullRow", hasFullRow);
        map.put("isModal", isModal);
        map.put("isPage", isPage);
        map.put("hasList", hasList);
        map.put("mainPath", mainPath);
        map.put("package", table.getPackageName());
        map.put("moduleName", table.getModuleName());
        map.put("author", table.getFunctionAuthor());
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));

        map.put("listColumns", listColumns);
        map.put("formColumns", formColumns);
        map.put("queryColumns", queryColumns);
        map.put("listDictColumns", StringUtil.join(listDictColumns, ","));
        map.put("formDictColumns", StringUtil.join(formDictColumns, ","));
        map.put("queryDictColumns", StringUtil.join(queryDictColumns, ","));
        dictCodes.addAll(listDictColumns);
        dictCodes.addAll(formDictColumns);
        dictCodes.addAll(queryDictColumns);
        List<String> codes = new ArrayList<>();
        dictCodes.forEach(x -> codes.add("'" + x + "'"));
        map.put("dictCodes", StringUtil.join(codes, ","));
        map.put("checkboxColumns", checkboxColumns);
        map.put("queryCascaderColumns", queryCascaderColumns);
        map.put("cascaderDictColumns", cascaderDictColumns);
        map.put("queryCheckboxColumns", queryCheckboxColumns);
        map.put("inputNumberColumns", inputNumberColumns);
        map.put("queryInputNumberColumns", queryInputNumberColumns);
        map.put("uploadColumns", uploadColumns);
        map.put("imageUploadColumns", imageUploadColumns);
        map.put("betweenColumns", betweenColumns);
        map.put("notInColumns", notInColumns);
        map.put("inColumns", inColumns);
        map.put("likeInAndColumns", likeInAndColumns);
        map.put("likeInOrColumns", likeInOrColumns);
        map.put("menu", table.getMenu());
        map.put("hasMenu", table.getMenu() != null);
        map.put("hasEditor", hasEditor);
        map.put("validateColumns", validateColumns);
        map.put("unValidateColumns", unValidateColumns);
        String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
        String replace = underlineCase.replace("_", "-");
        map.put("vueFileName", replace);
        String dir = replace;
        if (table.getMenu() != null) {
            dir = table.getMenu().getRouteName() + "/" + dir;
        }
        map.put("apiPath", dir + "/" + replace + "-api.js");
        map.put("i18n", i18n);
        map.put("i18nForm", i18nForm);

        map.put("addRoute",
                "        this.$router.push({\n" +
                        "           path: '/" + StringUtil.uncapitalize(table.getClassName()) + "Detail',\n" +
                        "           query: {\n" +
                        "               operateType: 'add'\n" +
                        "           } })"
        );
        map.put("editRoute",
                "        this.$router.push({\n" +
                        "           path: '/" + StringUtil.uncapitalize(table.getClassName()) + "Detail',\n" +
                        "           query: {\n" +
                        "               operateType: isRead ? 'info' : 'edit',\n" +
                        "               approvalStatus: record.approvalStatus,\n" +
                        "               id: record.id\n" +
                        "           } })"
        );
        map.put("checkRoute",
                "        this.$route.matched.forEach(x => {\n" +
                        "          if (x.meta && x.meta.isRv) {\n" +
                        "            this.customBreadcrumb = true\n" +
                        "          } else {\n" +
                        "            this.customRoutes.push({ path: x.path, title: x.meta.title })\n" +
                        "          }\n" +
                        "        })"
        );
        return new VelocityContext(map);
    }

    /**
     * 获取默认路径
     *
     * @param frontType 前端类型
     * @return List
     */
    private static List<Map<String, Object>> getDefault(String frontType) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> java = new LinkedHashMap<>();
        java.put("id", "Java");
        java.put("pId", "0");
        java.put("label", "Java");
        java.put("value", "Java");
        java.put("suffix", "");
        java.put("children", new ArrayList<Map<String, Object>>());
        list.add(java);

        Map<String, Object> controller = new LinkedHashMap<>();
        controller.put("id", "controller");
        controller.put("pId", "Java");
        controller.put("label", "controller");
        controller.put("value", "controller");
        controller.put("children", new ArrayList<Map<String, Object>>());
        list.add(controller);
        Map<String, Object> entity = new LinkedHashMap<>();
        entity.put("id", "entity");
        entity.put("pId", "Java");
        entity.put("label", "entity");
        entity.put("value", "entity");
        entity.put("children", new ArrayList<Map<String, Object>>());
        list.add(entity);
        Map<String, Object> service = new LinkedHashMap<>();
        service.put("id", "service");
        service.put("pId", "Java");
        service.put("label", "service");
        service.put("value", "service");
        service.put("children", new ArrayList<Map<String, Object>>());
        list.add(service);
        Map<String, Object> dao = new LinkedHashMap<>();
        dao.put("id", "dao");
        dao.put("pId", "Java");
        dao.put("label", "dao");
        dao.put("value", "dao");
        dao.put("children", new ArrayList<Map<String, Object>>());
        list.add(dao);
        if (StringUtil.notBlankAndEquals(frontType, NAIVE_MARK) || StringUtil.isBlank(frontType)) {
            // Naive vue
            Map<String, Object> vue = new LinkedHashMap<>();
            vue.put("id", "Naive Vue");
            vue.put("pId", "0");
            vue.put("label", "Naive Vue");
            vue.put("value", "Naive Vue");
            vue.put("suffix", "");
            vue.put("children", new ArrayList<Map<String, Object>>());
            list.add(vue);
            Map<String, Object> typings = new LinkedHashMap<>();
            typings.put("id", "typings");
            typings.put("pId", "Naive Vue");
            typings.put("label", "typings");
            typings.put("value", "typings");
            typings.put("children", new ArrayList<Map<String, Object>>());
            list.add(typings);
            Map<String, Object> api = new LinkedHashMap<>();
            api.put("id", "api");
            api.put("pId", "Naive Vue");
            api.put("label", "api");
            api.put("value", "api");
            api.put("children", new ArrayList<Map<String, Object>>());
            list.add(api);
            Map<String, Object> views = new LinkedHashMap<>();
            views.put("id", "views");
            views.put("pId", "Naive Vue");
            views.put("label", "views");
            views.put("value", "views");
            views.put("children", new ArrayList<Map<String, Object>>());
            list.add(views);
            Map<String, Object> settings = new LinkedHashMap<>();
            settings.put("id", "settings");
            settings.put("pId", "Naive Vue");
            settings.put("label", "settings");
            settings.put("value", "settings");
            settings.put("children", new ArrayList<Map<String, Object>>());
            list.add(settings);
            Map<String, Object> modules = new LinkedHashMap<>();
            modules.put("id", "modules");
            modules.put("pId", "views");
            modules.put("label", "modules");
            modules.put("value", "modules");
            modules.put("children", new ArrayList<Map<String, Object>>());
            list.add(modules);
            Map<String, Object> i18n = new LinkedHashMap<>();
            i18n.put("id", "i18n");
            i18n.put("pId", "settings");
            i18n.put("label", "i18n");
            i18n.put("value", "i18n");
            i18n.put("children", new ArrayList<Map<String, Object>>());
            list.add(i18n);
        }
        if (StringUtil.notBlankAndEquals(frontType, ANT_MARK) || StringUtil.isBlank(frontType)) {
            // ant-vue3
            Map<String, Object> antVue = new LinkedHashMap<>();
            antVue.put("id", "Ant Design Vue3");
            antVue.put("pId", "0");
            antVue.put("label", "Ant Design Vue3");
            antVue.put("value", "Ant Design Vue3");
            antVue.put("suffix", "");
            antVue.put("children", new ArrayList<Map<String, Object>>());
            list.add(antVue);

            Map<String, Object> antApi = new LinkedHashMap<>();
            antApi.put("id", "antApi");
            antApi.put("pId", "Ant Design Vue3");
            antApi.put("label", "api");
            antApi.put("value", "api");
            antApi.put("children", new ArrayList<Map<String, Object>>());
            list.add(antApi);

            Map<String, Object> antViews = new LinkedHashMap<>();
            antViews.put("id", "antViews");
            antViews.put("pId", "Ant Design Vue3");
            antViews.put("label", "views");
            antViews.put("value", "views");
            antViews.put("children", new ArrayList<Map<String, Object>>());
            list.add(antViews);

            Map<String, Object> antComponents = new LinkedHashMap<>();
            antComponents.put("id", "antComponents");
            antComponents.put("pId", "antViews");
            antComponents.put("label", "components");
            antComponents.put("value", "components");
            antComponents.put("children", new ArrayList<Map<String, Object>>());
            list.add(antComponents);
        }
        return list;

    }

    /**
     * 获取属性结构map
     *
     * @param template 模板
     * @param sw       writer
     * @param table    表单
     * @return Map
     */
    private static Map<String, Object> getMap(String template, String sw, GenTableEntity table) {
        Map<String, Object> dataMap = new LinkedHashMap<>();
        if (template.contains(ENTITY_JAVA)) {
            dataMap.put("id", ENTITY_JAVA);
            dataMap.put("pId", "entity");
            dataMap.put("label", table.getClassName() + "Entity.java");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(DAO_JAVA)) {
            dataMap.put("id", DAO_JAVA);
            dataMap.put("pId", "dao");
            dataMap.put("label", table.getClassName() + "Dao.java");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(SERVICE_JAVA)) {
            dataMap.put("id", SERVICE_JAVA);
            dataMap.put("pId", "service");
            dataMap.put("label", table.getClassName() + "Service.java");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(SERVICE_IMPL_JAVA)) {
            dataMap.put("id", SERVICE_IMPL_JAVA);
            dataMap.put("pId", "service");
            dataMap.put("label", table.getClassName() + "ServiceImpl.java");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(CONTROLLER_JAVA)) {
            dataMap.put("id", CONTROLLER_JAVA);
            dataMap.put("pId", "controller");
            dataMap.put("label", table.getClassName() + "Controller.java");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(DAO_XML)) {
            dataMap.put("id", DAO_XML);
            dataMap.put("pId", "dao");
            dataMap.put("label", table.getClassName() + "Dao.xml");
            dataMap.put("value", sw);
            return dataMap;
        }

        if (template.contains(ANT_MARK)) {
            if (template.contains(ANT_VIEWS_VUE)) {
                dataMap.put("id", ANT_MARK + "_" + ANT_VIEWS_VUE);
                dataMap.put("pId", "antViews");
                dataMap.put("label", "index.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(ANT_VIEWS_OPERATE_MODAL)) {
                dataMap.put("id", ANT_MARK + "_" + ANT_VIEWS_OPERATE_MODAL);
                dataMap.put("pId", "antComponents");
                dataMap.put("label", "operate-modal.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(ANT_VIEWS_OPERATE_DRAWER)) {
                dataMap.put("id", ANT_MARK + "_" + ANT_VIEWS_OPERATE_DRAWER);
                dataMap.put("pId", "antComponents");
                dataMap.put("label", "operate-drawer.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(ANT_COLUMNS)) {
                dataMap.put("id", ANT_MARK + "_" + ANT_COLUMNS);
                dataMap.put("pId", "antViews");
                dataMap.put("label", "columns.js");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(ANT_API)) {
                dataMap.put("id", ANT_MARK + "_" + ANT_API);
                dataMap.put("pId", "antApi");
                String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
                String replace = underlineCase.replace("_", "-");
                dataMap.put("label", replace + "-api.js");
                dataMap.put("value", sw);
                return dataMap;
            }
        } else {
            if (template.contains(SERVICE_TS)) {
                dataMap.put("id", SERVICE_TS);
                dataMap.put("pId", "api");
                dataMap.put("label", "index.ts");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(TYPINGS_TS)) {
                dataMap.put("id", TYPINGS_TS);
                dataMap.put("pId", "typings");
                dataMap.put("label", "index.d.ts");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(VIEWS_VUE)) {
                dataMap.put("id", VIEWS_VUE);
                dataMap.put("pId", "views");
                dataMap.put("label", "index.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(VIEWS_OPERATE_MODAL)) {
                dataMap.put("id", VIEWS_OPERATE_MODAL);
                dataMap.put("pId", "modules");
                String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
                String replace = underlineCase.replace("_", "-");
                dataMap.put("label", replace + "-operate-modal.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(VIEWS_OPERATE_DRAWER)) {
                dataMap.put("id", VIEWS_OPERATE_DRAWER);
                dataMap.put("pId", "modules");
                String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
                String replace = underlineCase.replace("_", "-");
                dataMap.put("label", replace + "-operate-drawer.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(VIEWS_SEARCH)) {
                dataMap.put("id", VIEWS_SEARCH);
                dataMap.put("pId", "modules");
                String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
                String replace = underlineCase.replace("_", "-");
                dataMap.put("label", replace + "-search.vue");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(SETTINGS_APP)) {
                dataMap.put("id", SETTINGS_APP);
                dataMap.put("pId", "i18n");
                dataMap.put("label", "app.d.ts");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(SETTINGS_ZH)) {
                dataMap.put("id", SETTINGS_ZH);
                dataMap.put("pId", "i18n");
                dataMap.put("label", "zh-cn.ts");
                dataMap.put("value", sw);
                return dataMap;
            }
            if (template.contains(SETTINGS_EN)) {
                dataMap.put("id", SETTINGS_EN);
                dataMap.put("pId", "i18n");
                dataMap.put("label", "en-us.ts");
                dataMap.put("value", sw);
                return dataMap;
            }
        }

        return null;
    }

    /**
     * 列名转换成Java属性名
     *
     * @param columnName 字段名
     * @return String
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName        表名
     * @param tablePrefixArray 忽略前缀
     * @return String
     */
    public static String tableToJava(String tableName, String[] tablePrefixArray) {
        if (null != tablePrefixArray) {
            for (String tablePrefix : tablePrefixArray) {
                if (tableName.startsWith(tablePrefix)) {
                    tableName = tableName.replaceFirst(tablePrefix, "");
                }
            }
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     *
     * @return Configuration
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new SmartException("获取配置文件失败");
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTableEntity table, boolean isGenerate) {
        String className = table.getClassName();
        String packageName = table.getPackageName();
        String moduleName = table.getModuleName();
        String packagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (StringUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }
        String resourcesPath = "smart-" + moduleName + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        String businessPath = "smart-" + moduleName + File.separator + packagePath + moduleName + File.separator;
        String entityPath = "smart-entity" + File.separator + packagePath;
        String servicePath = "smart-service" + File.separator + packagePath;
        String naivePath = "naive vue" + File.separator;
        String ant2Path = "ant design vue 2" + File.separator;
        String ant3Path = "ant design vue 3" + File.separator;
        String javaPath = "java" + File.separator;
        if (isGenerate) {
            String back = table.getOptions().getString(OPTION_JAVA_PATH);
            String front = table.getOptions().getString(OPTION_VUE_PATH);
            if (StringUtil.isBlank(back)) {
                throw new SmartException("后端目录获取异常");
            }
            String replaceBack = back.replace("\\", File.separator);
            if (!replaceBack.endsWith(File.separator)) {
                replaceBack = replaceBack + File.separator;
            }
            javaPath = replaceBack;
//            File backFile = new File(javaPath);
//            if (!backFile.exists() && !backFile.mkdirs()) {
//                throw new SmartException("后端目录创建出错！");
//            }

            if (StringUtil.isBlank(front)) {
                throw new SmartException("前端目录获取异常");
            }
            String replaceFront = front.replace("\\", File.separator);
            if (!replaceFront.endsWith(File.separator)) {
                replaceFront = replaceFront + File.separator;
            }
            ant2Path = replaceFront + "src" + File.separator;
            ant3Path = replaceFront + "src" + File.separator;
            naivePath = replaceFront + "src" + File.separator;


//            if (StringUtil.notBlankAndEquals(table.getFrontType(), NAIVE_MARK)) {
//                File frontFile = new File(naivePath);
//                if (!frontFile.exists() && !frontFile.mkdirs()) {
//                    throw new SmartException("前端目录创建出错！");
//                }
//            }
//            if (StringUtil.notBlankAndEquals(table.getFrontType(), ANT_MARK)) {
//                File frontFile = new File(ant3Path);
//                if (!frontFile.exists() && !frontFile.mkdirs()) {
//                    throw new SmartException("前端目录创建出错！");
//                }
//            }
        }
        if (template.contains(ENTITY_JAVA)) {
            return javaPath + entityPath + "entity" + File.separator + moduleName + File.separator + className + "Entity.java";
        }

        if (template.contains(DAO_JAVA)) {
            return javaPath + businessPath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains(SERVICE_JAVA)) {
            return javaPath + servicePath + "service" + File.separator + moduleName + File.separator + className + "Service.java";
        }

        if (template.contains(SERVICE_IMPL_JAVA)) {
            return javaPath + businessPath + "service" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains(CONTROLLER_JAVA)) {
            return javaPath + businessPath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(DAO_XML)) {
            return javaPath + resourcesPath + "mapper" + File.separator + className + "Dao.xml";
        }
        // v2
//        if (template.contains(LIST_VUE)) {
//            return ant2Path + "views" + File.separator + className.toLowerCase() + File.separator + "list.vue";
//        }
//        if (template.contains(FORM_VUE)) {
//            return ant2Path + "views" + File.separator + className.toLowerCase() + File.separator + "form.vue";
//        }
//        if (template.contains(API_JS)) {
//            return ant2Path + "api" + File.separator + className.toLowerCase() + ".js";
//        }
        // v3
        if (template.contains(ANT_MARK)) {
            // Ant Design vue3
            String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
            String res = underlineCase.replace("_", "-");
            String dir = res;
            if (table.getMenu() != null) {
                String routePath = table.getMenu().getRoutePath();
                if (StringUtil.isNotBlank(routePath)) {
                    if (routePath.startsWith("/")) {
                        routePath = routePath.substring(1);
                    }
                    routePath = routePath.replace("/", "\\");
                    dir = routePath + File.separator + dir;
                }
            }
            if (template.contains(API_JS)) {
                return ant3Path + "api" + File.separator + dir + File.separator + res + "-api.js";
            }
            if (template.contains(ANT_COLUMNS)) {
                return ant3Path + "views" + File.separator + dir + File.separator + "columns.js";
            }
            if (template.contains(ANT_VIEWS_VUE)) {
                return ant3Path + "views" + File.separator + dir + File.separator + "index.vue";
            }

            if (template.contains(ANT_VIEWS_OPERATE_MODAL)) {
                return ant3Path + "views" + File.separator + dir + File.separator + "components" + File.separator + "operate-modal.vue";
            }
            if (template.contains(ANT_VIEWS_OPERATE_DRAWER)) {
                return ant3Path + "views" + File.separator + dir + File.separator + "components" + File.separator + "operate-drawer.vue";
            }
        } else {
            // Naive vue
            String underlineCase = StrUtil.toUnderlineCase(table.getClassName());
            String res = underlineCase.replace("_", "-");
            String dir = res;
            if (table.getMenu() != null) {
                String routePath = table.getMenu().getRoutePath();
                if (StringUtil.isNotBlank(routePath)) {
                    if (routePath.startsWith("/")) {
                        routePath = routePath.substring(1);
                    }
                    routePath = routePath.replace("/", "\\");
                    dir = routePath + File.separator + dir;
                }
            }
            if (template.contains(TYPINGS_TS)) {
                return naivePath + "typings" + File.separator + dir + File.separator + "index.d.ts";
            }
            if (template.contains(SERVICE_TS)) {
                return naivePath + "service" + File.separator + "api" + File.separator + dir + File.separator + "index.ts";
            }
            if (template.contains(VIEWS_VUE)) {
                return naivePath + "views" + File.separator + dir + File.separator + "index.vue";
            }

            if (template.contains(VIEWS_OPERATE_MODAL)) {
                return naivePath + "views" + File.separator + dir + File.separator + "modules" + File.separator + res + "-operate-modal.vue";
            }

            if (template.contains(VIEWS_OPERATE_DRAWER)) {
                return naivePath + "views" + File.separator + dir + File.separator + "modules" + File.separator + res + "-operate-drawer.vue";
            }

            if (template.contains(VIEWS_SEARCH)) {
                return naivePath + "views" + File.separator + dir + File.separator + "modules" + File.separator + res + "-search.vue";
            }
        }
        return null;
    }

    /**
     * 获取 I18n
     *
     * @param columnName 字段名
     * @return String
     */
    private static String getI18nValue(String columnName) {
        if (StringUtil.isBlank(columnName)) {
            return null;
        }
        String[] s = columnName.split("_");
        StringBuilder sb = new StringBuilder();

        for (String string : s) {
            sb.append(StrUtil.upperFirst(string)).append(" ");
        }
        return sb.toString().trim();

    }

    /**
     * 获取 Placeholder
     *
     * @param columnName 字段名
     * @return String
     */
    private static String getPlaceholderValue(String columnName) {
        if (StringUtil.isBlank(columnName)) {
            return null;
        }
        String lowerCase = columnName.toLowerCase();
        return lowerCase.replace("_", " ");

    }

}
