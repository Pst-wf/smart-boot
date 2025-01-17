package com.smart.model.excel.fieldtype;

/**
 * Excel字段类型转换
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
public interface FieldType {

    /**
     * 获取对象值（导入）
     *
     * @param val 参数
     * @return Object
     */
    default Object getValue(String val) {
        return null;
    }

    /**
     * 获取对象值（导出）
     *
     * @param val 参数
     * @return Object
     */
    default String setValue(Object val) {
        return null;
    }

    /**
     * 获取对象值格式（导出）
     *
     * @return String
     */
    default String getDataFormat() {
        return null;
    }

}
