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
		@ExcelField(title = "输入框", attrName = "tableColumn1",  align = ExcelField.Align.CENTER, sort = 1),
		@ExcelField(title = "富文本", attrName = "tableColumn2",  align = ExcelField.Align.CENTER, sort = 2),
		@ExcelField(title = "多行文本", attrName = "tableColumn3",  align = ExcelField.Align.CENTER, sort = 3),
		@ExcelField(title = "选择框", attrName = "tableColumn4",  dictCode = "sys_dept_type", align = ExcelField.Align.CENTER, sort = 4),
		@ExcelField(title = "级联选择", attrName = "tableColumn5",  dictCode = "sys_tree_dict", align = ExcelField.Align.CENTER, sort = 5),
		@ExcelField(title = "树形选择", attrName = "tableColumn6",  dictCode = "sys_tree_dict", align = ExcelField.Align.CENTER, sort = 6),
		@ExcelField(title = "复选框", attrName = "tableColumn7",  dictCode = "sys_oss_type", align = ExcelField.Align.CENTER, sort = 7),
		@ExcelField(title = "单选框", attrName = "tableColumn8",  dictCode = "sys_oss_type", align = ExcelField.Align.CENTER, sort = 8),
		@ExcelField(title = "日期选择", attrName = "tableColumn9",  align = ExcelField.Align.CENTER, sort = 9),
		@ExcelField(title = "时间选择", attrName = "tableColumn10",  align = ExcelField.Align.CENTER, sort = 10),
		@ExcelField(title = "开关", attrName = "tableColumn11",  align = ExcelField.Align.CENTER, sort = 11),
		@ExcelField(title = "图片上传", attrName = "tableColumn13",  align = ExcelField.Align.CENTER, sort = 12),
		@ExcelField(title = "数字框", attrName = "tableColumn14",  align = ExcelField.Align.CENTER, sort = 13),
		@ExcelField(title = "创建人", attrName = "createUserName",  align = ExcelField.Align.CENTER, sort = 14),
		@ExcelField(title = "创建时间", attrName = "createDate",  align = ExcelField.Align.CENTER, sort = 15)
})
public class GenDemoEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 输入框
	 */
	@Column(name = "table_column1", queryType = QueryType.LIKE, isNull = false)
	private String tableColumn1;
	/**
	 * 富文本
	 */
	@Column(name = "table_column2", queryType = QueryType.LIKE, isNull = false)
	private String tableColumn2;
	/**
	 * 多行文本
	 */
	@Column(isNull = false)
	private String tableColumn3;
	/**
	 * 选择框
	 */
	@Column(name = "table_column4", queryType = QueryType.EQ, isNull = false)
	private String tableColumn4;
	/**
	 * 级联选择
	 */
	@Column(name = "table_column5", queryType = QueryType.EQ, isNull = false)
	private String tableColumn5;
	/**
	 * 树形选择
	 */
	@Column(name = "table_column6", queryType = QueryType.EQ, isNull = false)
	private String tableColumn6;
	/**
	 * 复选框
	 */
	private String tableColumn7;
	/**
	 * 单选框
	 */
	@Column(name = "table_column8", queryType = QueryType.EQ, isNull = false)
	private String tableColumn8;
	/**
	 * 日期选择
	 */
	private String tableColumn9;
	/**
	 * 时间选择
	 */
	@Column(isNull = false)
	private String tableColumn10;
	/**
	 * 开关
	 */
	@Column(isNull = false)
	private String tableColumn11;
	/**
	 * 文件上传
	 */
	@Column(isNull = false)
	private String tableColumn12;
	/**
	 * 图片上传
	 */
	@Column(isNull = false)
	private String tableColumn13;
	/**
	 * 数字上传
	 */
	@Column(name = "table_column14", queryType = QueryType.LIKE, isNull = false)
	private Integer tableColumn14;

	/**
	 * 复选框In查询集合
	 */
	@Column(name = "table_column7", queryType = QueryType.LIKE_IN_OR)
	@TableField(exist = false)
	private List<String> tableColumn7InList;

	/**
	 * 日期选择Between查询集合
	 */
	@Column(name = "table_column9", queryType = QueryType.BETWEEN)
	@TableField(exist = false)
	private List<String> tableColumn9BetweenList;

	/**
	 * 文件上传集合
	 */
	@TableField(exist = false)
	private List<FileEntity> tableColumn12List;

}
