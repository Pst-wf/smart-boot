package com.smart.service.system;

import com.smart.entity.system.ScopeEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;

/**
 * 数据权限 Service
 *
 * @author wf
 * @since 2022-01-23 18:13:52
 */
public interface ScopeService extends BaseService<ScopeEntity> {

    /**
     * 根据角色ID获取数据权限
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ScopeEntity> findScopesByRoleId(String roleId);
}

