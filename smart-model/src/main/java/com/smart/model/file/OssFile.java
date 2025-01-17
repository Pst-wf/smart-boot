package com.smart.model.file;

import lombok.Data;

import java.util.Date;

/**
 * Minio文件实体
 *
 * @author wf
 * @version 1.0.0
 * @since 2023/3/13
 */
@Data
public class OssFile {
    /**
     * 文件地址
     */
    private String link;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 哈希值
     */
    public String hash;
    /**
     * 长度
     */
    private long length;
    /**
     * 上传时间
     */
    private Date putTime;
    /**
     * 文件类型
     */
    private String contentType;
}
