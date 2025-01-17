package com.smart.entity.gen;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.enums.QueryType;
import com.smart.entity.file.FileEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.NoArgsConstructor;

/**
 * 生成案例
 *
 * @author wf
 * @since 2024-08-19 00:28:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("gen_demo")
@ExcelFields({
        @ExcelField(title = "T1", attrName = "tableColumn1",  align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "T2", attrName = "tableColumn2",  align = ExcelField.Align.CENTER, sort = 2),
        @ExcelField(title = "T3", attrName = "tableColumn3",  align = ExcelField.Align.CENTER, sort = 3),
        @ExcelField(title = "T4", attrName = "tableColumn4",  dictCode = "sys_dept_type", align = ExcelField.Align.CENTER, sort = 4),
        @ExcelField(title = "T5", attrName = "tableColumn5",  dictCode = "sys_tree_dict", align = ExcelField.Align.CENTER, sort = 5),
        @ExcelField(title = "T6", attrName = "tableColumn6",  dictCode = "sys_tree_dict", align = ExcelField.Align.CENTER, sort = 6),
        @ExcelField(title = "T7", attrName = "tableColumn7",  dictCode = "sys_oss_type", align = ExcelField.Align.CENTER, sort = 7),
        @ExcelField(title = "T8", attrName = "tableColumn8",  dictCode = "sys_oss_type", align = ExcelField.Align.CENTER, sort = 8),
        @ExcelField(title = "T9", attrName = "tableColumn9",  align = ExcelField.Align.CENTER, sort = 9),
        @ExcelField(title = "T10", attrName = "tableColumn10",  align = ExcelField.Align.CENTER, sort = 10),
        @ExcelField(title = "T11", attrName = "tableColumn11",  align = ExcelField.Align.CENTER, sort = 11),
        @ExcelField(title = "T13", attrName = "tableColumn13",  align = ExcelField.Align.CENTER, sort = 12),
        @ExcelField(title = "T14", attrName = "tableColumn14",  align = ExcelField.Align.CENTER, sort = 13),
        @ExcelField(title = "创建人", attrName = "createUserName",  align = ExcelField.Align.CENTER, sort = 14),
        @ExcelField(title = "创建时间", attrName = "createDate",  align = ExcelField.Align.CENTER, sort = 15)
})
public class GenDemoEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * T1
	 */
    @Column(name = "table_column1", queryType = QueryType.LIKE, isNull = false)
    private String tableColumn1;
	/**
	 * T2
	 */
    @Column(name = "table_column2", queryType = QueryType.LIKE, isNull = false)
    private String tableColumn2;
	/**
	 * T3
	 */
    @Column(isNull = false)
    private String tableColumn3;
	/**
	 * T4
	 */
    @Column(name = "table_column4", queryType = QueryType.EQ, isNull = false)
    private String tableColumn4;
	/**
	 * T5
	 */
    @Column(name = "table_column5", queryType = QueryType.EQ, isNull = false)
    private String tableColumn5;
	/**
	 * T6
	 */
    @Column(name = "table_column6", queryType = QueryType.EQ, isNull = false)
    private String tableColumn6;
	/**
	 * T7
	 */
    private String tableColumn7;
	/**
	 * T8
	 */
    @Column(name = "table_column8", queryType = QueryType.EQ, isNull = false)
    private String tableColumn8;
	/**
	 * T9
	 */
    private String tableColumn9;
	/**
	 * T10
	 */
    @Column(isNull = false)
    private String tableColumn10;
	/**
	 * T11
	 */
    @Column(isNull = false)
    private String tableColumn11;
	/**
	 * T12
	 */
    @Column(isNull = false)
    private String tableColumn12;
	/**
	 * T13
	 */
    @Column(isNull = false)
    private String tableColumn13;
	/**
	 * T14
	 */
    @Column(name = "table_column14", queryType = QueryType.LIKE, isNull = false)
    private Integer tableColumn14;

    /**
     * T7In查询集合
     */
    @Column(name = "table_column7", queryType = QueryType.IN)
    @TableField(exist = false)
    private List<String> tableColumn7InList;

    /**
     * T9Between查询集合
     */
    @Column(name = "table_column9", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    private List<String> tableColumn9BetweenList;

    /**
     * t12
     */
    @TableField(exist = false)
    private List<FileEntity> tableColumn12List;

}
