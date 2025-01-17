package com.smart.system.dao;

import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.system.ButtonsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 按钮表
 *
 * @author wf
 * @since 2024-06-16 21:16:44
 */
@Mapper
public interface ButtonsDao extends BaseDao<ButtonsEntity> {
    /**
     * 根据角色ID获取按钮
     *
     * @param roleId 角色ID
     * @return List
     */
    List<ButtonsEntity> findButtonsByRoleId(String roleId);
}
