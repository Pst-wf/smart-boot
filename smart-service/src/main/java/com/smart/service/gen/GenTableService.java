package com.smart.service.gen;


import com.smart.entity.gen.GenTableEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 代码生成表 Service
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface GenTableService extends BaseService<GenTableEntity> {

    /**
     * 生成代码
     *
     * @param ids 要生成的表的IDS
     * @return byte[]
     */
    byte[] generatorCode(List<String> ids);

    /**
     * 获取所有表信息
     *
     * @return List
     */
    List<GenTableEntity> findTables();

    /**
     * 预览
     *
     * @param id 要生成的表的ID
     * @return List
     */
    List<Map<String, Object>> previewCode(String id);
}

