package com.smart.entity.system;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
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
 * 机构
 *
 * @author wf
 * @since 2022-01-15 00:21:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName(value = "sys_dept")
@ExcelFields({
        @ExcelField(title = "机构编号", attrName = "deptCode", align = ExcelField.Align.CENTER, words = 20, sort = 10),
        @ExcelField(title = "机构类型", attrName = "deptType", dictCode = "sys_dept_type", align = ExcelField.Align.CENTER, sort = 20),
        @ExcelField(title = "机构名称", attrName = "deptName", align = ExcelField.Align.CENTER, words = 80, sort = 30),
        @ExcelField(title = "所属机构", attrName = "parentName", align = ExcelField.Align.CENTER, words = 80, sort = 40, type = ExcelField.Type.EXPORT),
})
public class DeptEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 机构编号
     */
    @Column(name = "dept_code", queryType = QueryType.LIKE)
    private String deptCode;

    /**
     * 父级ID
     */
    @Column(name = "parent_id", queryType = QueryType.EQ)
    private String parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 机构类型
     */
    @Column(name = "dept_type", queryType = QueryType.EQ)
    private String deptType;

    /**
     * 机构名称
     */
    @Column(name = "dept_name", queryType = QueryType.LIKE)
    private String deptName;

    /**
     * 启用状态
     */
    @Column(queryType = QueryType.EQ)
    private String status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子集
     */
    @TableField(exist = false)
    private List<DeptEntity> children;

    /**
     * 所属机构名称
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 子集数量
     */
    @TableField(exist = false)
    private Long childCount;


    /**
     * 是否更换父级ID
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private Boolean isChangeParentId;
}
