package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.OssEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 对象存储
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface OssDao extends BaseDao<OssEntity> {
    /**
     * 修改启用状态
     *
     * @param ossStatus 启用状态
     * @param id        主键ID
     * @return Boolean
     */
    boolean updateOssStatus(@Param("ossStatus") String ossStatus, @Param("id") String id);
}
