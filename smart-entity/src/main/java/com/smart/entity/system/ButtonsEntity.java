package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseIdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 按钮表
 *
 * @author wf
 * @since 2024-06-16 21:16:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_buttons")
public class ButtonsEntity extends BaseIdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 按钮编号
     */
    private String code;

    /**
     * 按钮描述
     */
    private String name;

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 排序
     */
    private Integer sort;

}
