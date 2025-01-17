package com.smart.model.file;

import lombok.Data;

import java.io.Serializable;


/**
 * 回调模型 (在线编辑)
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Data
public class DocumentCallBackVO implements Serializable {

    /**
     * 定义文档的状态。 可以有以下值：
     * 1 - 正在编辑文档，
     * 2 - 文档已准备好保存，
     * 3 - 发生文档保存错误，
     * 4 - 文档已关闭，没有任何更改，
     * 6 - 正在编辑文档，但保存了当前文档状态，
     * 7 - 强制保存文档时发生错误。
     */
    private Integer status;

    /**
     * 定义已编辑的要由文档存储服务保存的文档的链接。 仅当 status 值等于 2, 3, 6 或 7 时，链接才存在。
     */
    private String url;

}