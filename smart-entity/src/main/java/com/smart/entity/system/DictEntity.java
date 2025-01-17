package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 字典
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_dict")
public class DictEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 父级ID
     */
    @Column(name = "parent_id", queryType = QueryType.EQ)
    private String parentId;

    /**
     * 祖级列表
     */
    @Column(name = "ancestors", queryType = QueryType.LIKE)
    private String ancestors;

    /**
     * 字典名称
     */
    @Column(name = "dict_name", queryType = QueryType.LIKE, isNull = false)
    private String dictName;

    /**
     * 类型 1系统 2业务
     */
    @Column(name = "dict_type", queryType = QueryType.EQ)
    private String dictType;

    /**
     * 字典编码
     */
    @Column(name = "dict_code", queryType = QueryType.LIKE)
    private String dictCode;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子集
     */
    @TableField(exist = false)
    private List<DictEntity> children;
    /**
     * 值集合
     */
    @TableField(exist = false)
    private List<DictEntity> values;

    /**
     * 前端比对用
     */
    @TableField(exist = false)
    private String oldDictName;
    @TableField(exist = false)
    private String oldDictValue;
    @TableField(exist = false)
    private Integer oldSort;

}
