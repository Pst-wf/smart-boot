package com.smart.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形工具类
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/6/29
 */
public class TreeUtil {
    /**
     * 构建树形
     *
     * @param list 需要构建的数据
     * @return List
     * @apiNote id parentId children
     */
    public static <E> List<E> buildTree(List<E> list) {
        Map<String, E> map = new HashMap<>();
        List<E> parentList = new ArrayList<>();
        List<E> result = new ArrayList<>();
        list.forEach(e -> {
            map.put(ReflectUtil.getFieldValue(e, "id"), e);
        });
        for (E item : list) {
            //根据父id去拿构建的树
            String parentId = ReflectUtil.getFieldValue(item, "parentId");
            E parent = map.get(parentId);
            //根据子id去拿构建的树
            String id = ReflectUtil.getFieldValue(item, "id");
            E dept = map.get(id);
            //为空了说明根据父id去拿没拿到，那么父id为null,本次循环找到了顶级
            if (parent == null) {
                //那么就把这个顶级数据的树放到list里面存起来
                parentList.add(dept);
                continue;
            }
            //如果不是顶级数据，我们就把本数据id构建的树，放到父id构建树的里面的list里面。这样我们就可以把所有的数据全部组装成父子关系
            List<E> children = ReflectUtil.getFieldValue(parent, "children");
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(dept);
            ReflectUtil.setFieldValue(parent, "children", children);
            map.put(ReflectUtil.getFieldValue(parent, "id"), parent);
        }
        parentList.forEach(p -> {
            String id = ReflectUtil.getFieldValue(p, "id");
            result.add(map.get(id));
        });
        return result;
    }

    /**
     * 构建树形
     *
     * @param list 需要构建的数据
     * @return List
     */
    public static List<Map<String, Object>> buildTreeMap(List<Map<String, Object>> list, String index, String parentIndex) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<Map<String, Object>> parentList = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        list.forEach(e -> {
            map.put((String) e.get(index), e);
        });
        for (Map<String, Object> item : list) {
            //根据父id去拿构建的树
            Map<String, Object> parent = map.get((String) item.get(parentIndex));
            //根据子id去拿构建的树
            Map<String, Object> value = map.get((String) item.get(index));
            //为空了说明根据父id去拿没拿到，那么父id为null,本次循环找到了顶级
            if (parent == null) {
                //那么就把这个顶级数据的树放到list里面存起来
                parentList.add(value);
                continue;
            }
            //如果不是顶级数据，我们就把本数据id构建的树，放到父id构建树的里面的list里面。这样我们就可以把所有的数据全部组装成父子关系
            List<Map<String, Object>> children = (List) parent.get("children");
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(value);
            parent.put("children", children);
            map.put((String) parent.get(index), parent);
        }
        parentList.forEach(p -> {
            result.add(map.get((String) p.get(index)));
        });
        return result;
    }

    /**
     * 树形平铺
     *
     * @param trees 需要平铺的数据
     * @return List
     */
    public static <T> List<T> toList(List<T> trees) {
        List<T> datalist = new ArrayList<>();
        if (ListUtil.isNotEmpty(trees)) {
            for (T t : trees) {
                // 设置直接点
                List<T> childTree = ReflectUtil.getFieldValue(t, "children");
                if (!ListUtil.isEmpty(childTree)) {
                    List<T> childList = toList(childTree);
                    if (!ListUtil.isEmpty(childList)) {
                        datalist.addAll(childList);
                    }
                }
                datalist.add(t);
            }
        }
        return datalist;
    }
}
