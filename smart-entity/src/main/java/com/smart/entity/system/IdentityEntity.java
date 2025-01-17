package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseIdEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 身份
 *
 * @author wf
 * @since 2022-01-14 22:38:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_identity")
public class IdentityEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Column(name = "user_id", queryType = QueryType.EQ, isNull = false)
    private String userId;

    /**
     * 岗位ID
     */
    @Column(name = "post_id", queryType = QueryType.EQ, isNull = false)
    private String postId;

    /**
     * 角色ID
     */
    @Column(name = "role_id", queryType = QueryType.EQ, isNull = false)
    private String roleId;

    /**
     * 部门ID
     */
    @Column(name = "dept_id", queryType = QueryType.EQ, isNull = false)
    private String deptId;

    /**
     * 机构ID
     */
    @Column(name = "organization_id", queryType = QueryType.EQ, isNull = false)
    private String organizationId;

    /**
     * 状态（0正常 1删除)
     */
    @TableField(fill = FieldFill.INSERT)
    public String isDeleted;

    /**
     * 机构名称
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 岗位名称
     */
    @TableField(exist = false)
    private String postName;

    /**
     * 机构名称
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 机构名称
     */
    @TableField(exist = false)
    private String organizationName;

    /**
     * 角色
     */
    @TableField(exist = false)
    private RoleEntity roleEntity;

    /**
     * 所属部门祖先
     */
    @TableField(exist = false)
    private String deptAncestors;

}
