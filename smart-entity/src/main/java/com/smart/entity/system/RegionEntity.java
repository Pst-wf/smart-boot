package com.smart.entity.system;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 行政区域
 *
 * @author wf
 * @since 2025-02-20 16:59:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_region")
@ExcelFields({
        @ExcelField(title = "级别", attrName = "level", align = ExcelField.Align.CENTER, sort = 2),
        @ExcelField(title = "父级ID", attrName = "parentId", align = ExcelField.Align.CENTER, sort = 3),
        @ExcelField(title = "行政编号", attrName = "areaCode", align = ExcelField.Align.CENTER, sort = 4),
        @ExcelField(title = "名称", attrName = "name", align = ExcelField.Align.CENTER, sort = 5),
        @ExcelField(title = "合并名称", attrName = "mergerName", align = ExcelField.Align.CENTER, sort = 6),
        @ExcelField(title = "经度", attrName = "lng", align = ExcelField.Align.CENTER, sort = 7),
        @ExcelField(title = "纬度", attrName = "lat", align = ExcelField.Align.CENTER, sort = 8),
        @ExcelField(title = "启用状态 ", attrName = "status", align = ExcelField.Align.CENTER, sort = 9)
})
public class RegionEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 级别
     */
    private Integer level;
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
     * 行政编号
     */
    @Column(name = "area_code", queryType = QueryType.LIKE)
    private String areaCode;
    /**
     * 名称
     */
    @Column(name = "name", queryType = QueryType.LIKE)
    private String name;
    /**
     * 合并名称
     */
    private String mergerName;
    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;
    /**
     * 启用状态
     */
    @Column(isNull = false)
    private String status;
    /**
     * 是否最末级
     */
    @TableField(exist = false)
    private Boolean isLeaf;
    /**
     * 子集
     */
    @TableField(exist = false)
    private List<RegionEntity> children;

    /**
     * 是否更换父级ID
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private Boolean isChangeParentId;
}
