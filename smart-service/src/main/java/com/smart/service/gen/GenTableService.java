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
     * @param entity 参数
     * @return byte[]
     */
    byte[] generatorCode(GenTableEntity entity);

    /**
     * 生成代码到指定目录
     *
     * @param entity 参数
     */
    void generatorCodeInFile(GenTableEntity entity);

    /**
     * 获取所有表信息
     *
     * @return List
     */
    List<GenTableEntity> findTables();

    /**
     * 预览
     *
     * @param id        要生成的表的ID
     * @param frontType 前端类型
     * @return List
     */
    List<Map<String, Object>> previewCode(String id, String frontType);

    /**
     * 获取JAVA工程根目录
     *
     * @return String
     */
    String getWorkSpace();
}

