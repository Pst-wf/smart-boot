package com.smart.entity.system;


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

/**
 * 用户配置
 *
 * @author wf
 * @since 2024-11-05 10:18:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_user_config")
@ExcelFields({
        @ExcelField(title = "用户ID", attrName = "userId",  align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "用户配置", attrName = "configValue",  align = ExcelField.Align.CENTER, sort = 2)
})
public class UserConfigEntity extends BaseIdEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
    @Column(isNull = false)
    private String id;
	/**
	 * 用户ID
	 */
    @Column(name = "USER_ID", queryType = QueryType.EQ)
    private String userId;
	/**
	 * 用户配置
	 */
    private String configValue;
	/**
	 * naive-ui用户配置
	 */
	private String naiveUiConfigValue;
	/**
	 * 国际化配置
	 */
    private String langValue;
	/**
	 * 快捷入口配置
	 */
	private String quickEntryValue;
	/**
	 * naive-ui 快捷入口配置
	 */
	private String naiveUiQuickEntryValue;
}
