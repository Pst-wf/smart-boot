package com.smart.file.config;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.smart.common.utils.IOUtil;
import com.smart.model.file.DocumentType;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件类型配置 (在线编辑)
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Data
@Configuration
public class DocumentOnlineConfig {

    @Bean("documentTypeMap")
    public Map<String, String> documentTypeMap() {
        Map<String, String> documentTypeMap = new HashMap<String, String>();
        String value = IOUtil.getValue("json/documentType.json");
        JSONArray jsonArray = JSONUtil.parseArray(value);
        List<DocumentType> documentTypes = JSONUtil.toList(jsonArray, DocumentType.class);
        for (DocumentType documentType : documentTypes) {
            List<String> acceptType = documentType.getAcceptType();
            for (String accept : acceptType) {
                documentTypeMap.put(accept, documentType.getDocumentType());
            }
        }
        return documentTypeMap;
    }
}
