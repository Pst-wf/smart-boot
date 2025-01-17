package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.IdentityEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 身份
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface IdentityDao extends BaseDao<IdentityEntity> {

    /**
     * 获取身份信息
     *
     * @param id 身份ID
     * @return String
     */
    IdentityEntity getInfo(String id);
}
