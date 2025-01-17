package com.smart.model.route;

import lombok.Data;

/**
 * 按钮
 *
 * @author wf
 * @version 1.0.0
 * @since 2024/6/27
 */
@Data
public class Button {
    /**
     * 按钮编号
     */
    private String code;

    /**
     * 按钮描述
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;
}
