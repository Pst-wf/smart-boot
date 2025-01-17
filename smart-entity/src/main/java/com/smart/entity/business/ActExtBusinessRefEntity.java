package com.smart.entity.business;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.NoArgsConstructor;

/**
 * 流程业务类别-流程定义 中间表
 *
 * @author wf
 * @since 2024-10-24 13:40:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("act_ext_business_ref")
@ExcelFields({
        @ExcelField(title = "业务类别ID", attrName = "businessId",  align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "流程定义ID", attrName = "processDefinitionId",  align = ExcelField.Align.CENTER, sort = 2)
})
public class ActExtBusinessRefEntity extends BaseIdEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 业务类别ID
	 */
    @Column(name = "business_id", queryType = QueryType.EQ, isNull = false)
    private String businessId;
	/**
	 * 流程定义ID
	 */
    @Column(name = "process_definition_id", queryType = QueryType.EQ, isNull = false)
    private String processDefinitionId;

}
