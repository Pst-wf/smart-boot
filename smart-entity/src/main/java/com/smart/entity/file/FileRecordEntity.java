package com.smart.entity.file;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 文件中心
 *
 * @author wf
 * @since 2023-04-26 16:02:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_file_record")
public class FileRecordEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件key
     */
    private String fileKey;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private BigDecimal fileSize;

    /**
     * 文件地址
     */
    private String filePath;

}
