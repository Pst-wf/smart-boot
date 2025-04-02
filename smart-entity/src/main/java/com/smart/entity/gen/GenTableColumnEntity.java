package com.smart.entity.gen;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 代码生成表列
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_gen_table_column")
public class GenTableColumnEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @Column(queryType = QueryType.EQ)
    private String tableId;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列排序（升序）
     */
    private BigDecimal columnSort;

    /**
     * 类型
     */
    private String columnType;

    /**
     * 长度
     */
    private String columnLength;

    /**
     * 列备注说明
     */
    private String comments;

    /**
     * 类的属性名
     */
    private String attrName;

    /**
     * 类的属性类型
     */
    private String attrType;

    /**
     * 是否主键
     */
    private String isPk;

    /**
     * 是否不为空
     */
    private String isNotNull;

    /**
     * 查询方式
     */
    private String queryType;

    /**
     * 其它生成选项
     */
    private String options;

    /**
     * 是否列表
     */
    private String isList;

    /**
     * 是否可编辑
     */
    private String isForm;

    /**
     * 组件
     */
    private String components;

    /**
     * 字典ID
     */
    private String dictCode;

    /**
     * 国际化英文
     */
    @TableField(exist = false)
    private String i18nValue;

    /**
     * 提示消息
     */
    @TableField(exist = false)
    private String placeholderValue;

    /**
     * 行样式
     */
    private String rowStyle;

    /**
     * 表名
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String tableName;

    /**
     * 大写开头的字段名
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String attrNameBig;

    /**
     * 上传关联字段
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private GenTableColumnEntity refUpload;

    /**
     * 表的主键
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private GenTableColumnEntity pk;

    /**
     * 是否默认字段
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private Boolean isDefault;

    /**
     * 是否是数字
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private Boolean isNumber;

    /**
     * 是否校验长度
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private Boolean isCheckLength;
}
