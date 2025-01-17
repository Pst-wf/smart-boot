package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.ScopeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据权限
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface ScopeDao extends BaseDao<ScopeEntity> {

    /**
     * 根据角色ID获取数据权限
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ScopeEntity> findScopesByRoleId(String roleId);
}
