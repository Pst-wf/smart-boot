package com.smart.gen.dao;

import com.smart.entity.gen.GenTableEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 代码生成表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface GenTableDao extends BaseDao<GenTableEntity> {

    List<GenTableEntity> findTables();
}
