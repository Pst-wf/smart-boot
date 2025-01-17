package com.smart.mybatis.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.smart.common.annotation.ExcludeKeys;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.List;

/**
 * 基础表 (只有ID)
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor()
@FieldNameConstants
@ExcludeKeys({"deleteIds"})
public class BaseIdEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    public String id;

    /**
     * 条数
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public int size;

    /**
     * 页数
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public int current;

    /**
     * IDS
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public List<String> ids;

    /**
     * 选择IDS
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    @Column(name = "id", queryType = QueryType.IN)
    public List<String> selectIds;

    /**
     * 删除IDS
     */
    @TableField(exist = false)
    public List<String> deleteIds;

    /**
     * 排序字段
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public String sortField;

    /**
     * 排序规则
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public String sortOrder;

    /**
     * 是否自动填充
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public Boolean auto = true;

    /**
     * 是否自动追加默认字段排序
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    public Boolean defaultOrder = true;
}
