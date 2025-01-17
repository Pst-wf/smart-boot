package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.NoArgsConstructor;

/**
 * 角色-按钮
 *
 * @author wf
 * @since 2024-06-20 11:17:34
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_role_buttons")
public class RoleButtonsEntity extends BaseIdEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
    @Column(isNull = false)
    private String roleId;

	/**
	 * 按钮code
	 */
    @Column(isNull = false)
    private String buttonCode;

}
