package com.smart.model.excel.fieldtype;


import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * 金额类型转换（保留两位）
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
public class MoneyType implements FieldType {

    private final NumberFormat nf = new DecimalFormat(",##0.00");

    /**
     * 获取对象值（导入）
     */
    @Override
    public Object getValue(String val) {
        return val == null ? StringUtils.EMPTY : StringUtils.replace(val, "," , StringUtils.EMPTY);
    }

    /**
     * 获取对象值（导出）
     */
    @Override
    public String setValue(Object val) {
        return val == null ? StringUtils.EMPTY : nf.format(val);
    }

    /**
     * 获取对象值格式（导出）
     */
    @Override
    public String getDataFormat() {
        return "0.00";
    }

}
