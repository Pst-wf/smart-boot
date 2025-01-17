package com.smart.mybatis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BaseDao
 *
 * @author wf
 * @since 2022-08-08
 */
public interface BaseDao<T> extends BaseMapper<T> {
    /**
     * 物理删除
     *
     * @param tableName 表名
     * @param ids 删除ids
     */
    int realDelete(@Param("tableName") String tableName, @Param("ids") List<String> ids);
}