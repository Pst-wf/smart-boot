package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 角色-数据权限
 *
 * @author wf
 * @since 2022-01-23 18:13:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_role_scope")
public class RoleScopeEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 数据权限ID
     */
    private String scopeId;

    /**
     * 角色ID
     */
    private String roleId;

}
