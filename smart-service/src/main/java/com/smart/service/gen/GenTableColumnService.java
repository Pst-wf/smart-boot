package com.smart.service.gen;


import com.smart.entity.gen.GenTableColumnEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;

/**
 * 代码生成表列 Service
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface GenTableColumnService extends BaseService<GenTableColumnEntity> {

    /**
     * 通过表名获取数据库中的字段
     *
     * @param genTableColumnEntity 字段bean
     * @return List
     */
    List<GenTableColumnEntity> findColumns(GenTableColumnEntity genTableColumnEntity);
}

