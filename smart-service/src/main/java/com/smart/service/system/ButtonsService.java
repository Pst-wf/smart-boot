package com.smart.service.system;

import com.smart.entity.system.ButtonsEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;

/**
 * 按钮表 Service
 *
 * @author wf
 * @since 2024-06-16 21:16:44
 */
public interface ButtonsService extends BaseService<ButtonsEntity> {
    /**
     * 根据角色ID获取按钮
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ButtonsEntity> findButtonsByRoleId(String roleId);
}

