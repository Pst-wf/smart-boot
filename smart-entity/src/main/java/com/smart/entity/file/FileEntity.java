package com.smart.entity.file;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.util.List;

/**
 * 文件中心
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_file")
public class FileEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 文件key
     */
    private String fileKey;

    /**
     * 文件名称
     */
    @Column(name = "file_name" , queryType = QueryType.LIKE)
    private String fileName;

    /**
     * 文件大小
     */
    private BigDecimal fileSize;

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 是否可编辑
     */
    private String isEditable;
    /**
     * 是否文本可编辑
     */
    private String isTextEditable;

    /**
     * 响应头类型
     */
    private String contentType;

    /**
     * 上传方式
     */
    private String uploadType;

    /**
     * 业务类型
     */
    @Column(name = "ref_type" , queryType = QueryType.LIKE)
    private String refType;

    /**
     * 业务ID
     */
    private String refId;

    /**
     * 删除的keys
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    private List<String> fileKeys;

}
