package com.smart.common.utils;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JSON工具类
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/2/21
 */
public class JsonUtil {

    public static List<SerializeFilter> getFilterList(Object o, Class<? extends Annotation> clazz, String... keys) {
        List<SerializeFilter> filterList = new ArrayList<>();
        PropertyPreFilters filters = new PropertyPreFilters();
        filterList.add(JsonUtil.getFilter(filters, o.getClass(), clazz, keys));
        List<Class<?>> childList = ObjectUtil.getObjectWithTableName(o, clazz);
        for (Class<?> c : childList) {
            filterList.add(JsonUtil.getFilter(filters, c, clazz, keys));
        }
        return filterList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<SerializeFilter> getFilterList(Class<?> clazz, String... keys) {
        List<SerializeFilter> filterList = new ArrayList<>();
        PropertyPreFilters filters = new PropertyPreFilters();
        filterList.add(JsonUtil.getFilter(filters, clazz, keys));
        return filterList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 生成过滤器
     *
     * @param filters 过滤器
     * @param clazz   需要过滤的class
     * @return PropertyPreFilters.MySimplePropertyPreFilter
     */
    public static PropertyPreFilters.MySimplePropertyPreFilter getFilter(PropertyPreFilters filters, Class<?> clazz) {
        TableName tableName = clazz.getAnnotation(TableName.class);
        String[] propertyName = tableName.excludeProperty();
        return getFilterExcludesByFileNames(filters, clazz, propertyName);
    }

    /**
     * 生成过滤器
     *
     * @param filters 过滤器
     * @param clazz   需要过滤的class
     * @return PropertyPreFilters.MySimplePropertyPreFilter
     */
    public static PropertyPreFilters.MySimplePropertyPreFilter getFilter(PropertyPreFilters filters, Class<?> clazz, Class<? extends Annotation> annotation, String... keys) {
        List<? extends Annotation> list = ReflectUtil.getAnnotationFromSuperclass(clazz, annotation);
        if (keys != null && keys.length > 0 && !list.isEmpty()) {
            List<String> propertyNames = new ArrayList<>();
            for (Annotation a : list) {
                for (String key : keys) {
                    String[] propertyName = ReflectUtil.invokeAnnotationGetter(a, key);
                    if (propertyName != null) {
                        propertyNames.addAll(Arrays.asList(propertyName));
                    }
                }
            }
            return getFilterExcludesByFileNames(filters, clazz, propertyNames.toArray(new String[0]));
        }
        return null;
    }

    /**
     * 生成过滤器
     *
     * @param filters 过滤器
     * @param clazz   需要过滤的class
     * @return PropertyPreFilters.MySimplePropertyPreFilter
     */
    public static PropertyPreFilters.MySimplePropertyPreFilter getFilter(PropertyPreFilters filters, Class<?> clazz, String... keys) {
        if (keys != null && keys.length > 0) {
            List<String> propertyNames = new ArrayList<>(Arrays.asList(keys));
            return getFilterExcludesByFileNames(filters, clazz, propertyNames.toArray(new String[0]));
        }
        return null;
    }

    /**
     * 通过入参生成过滤器生成过滤器
     *
     * @param filters  过滤器
     * @param clazz    需要过滤的class
     * @param excludes 需要过滤的字段名
     * @return PropertyPreFilters.MySimplePropertyPreFilter
     */
    public static PropertyPreFilters.MySimplePropertyPreFilter getFilterExcludesByFileNames(PropertyPreFilters filters, Class<?> clazz, String... excludes) {
        PropertyPreFilters.MySimplePropertyPreFilter exclude = filters.addFilter(clazz);
        exclude.addExcludes(excludes);
        return exclude;
    }
}
