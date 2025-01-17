package com.smart.model.terminal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Ssh请求VO
 *
 * @author wf
 * @version 1.0.0
 * @since 2022/11/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class SshRequestVO implements Serializable {
    private String sshId;
    private String path;
    private Integer level;
    private String key;
    private String fileName;
    private Boolean init;
    private MultipartFile file;
}
