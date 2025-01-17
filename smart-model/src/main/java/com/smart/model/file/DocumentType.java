package com.smart.model.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件类型 (在线编辑)
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Data
public class DocumentType implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 文档类型
     */
    private String documentType;

    /**
     * 名称
     */
    private String name;

    /**
     * 接受类型
     */
    private List<String> acceptType;
}
