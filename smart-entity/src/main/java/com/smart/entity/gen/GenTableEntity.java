package com.smart.entity.gen;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.entity.system.MenuEntity;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import com.smart.mybatis.handler.FastJSONObjectTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * 代码生成表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_gen_table")
public class GenTableEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    @Column(queryType = QueryType.LIKE)
    private String tableName;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * 表说明
     */
    private String comments;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成功能作者
     */
    private String functionAuthor;

    /**
     * 其它生成选项
     */
    @TableField(typeHandler = FastJSONObjectTypeHandler.class)
    private JSONObject options;
    /**
     * 所属菜单ID
     */
    private String menuId;

    /**
     * 所属菜单
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private MenuEntity menu;

    /**
     * 字段
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private List<GenTableColumnEntity> columns;

}
