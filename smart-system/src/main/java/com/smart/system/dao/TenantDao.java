package com.smart.system.dao;

import com.smart.entity.system.TenantEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;


/**
 * 租户
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@Mapper
public interface TenantDao extends BaseDao<TenantEntity> {

}
