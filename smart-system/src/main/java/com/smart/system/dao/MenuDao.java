package com.smart.system.dao;

import com.smart.entity.system.MenuEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface MenuDao extends BaseDao<MenuEntity> {

    /**
     * 通过角色ID获取菜单集合
     *
     * @param roleId 角色ID
     * @return List
     */
    List<MenuEntity> findMenuByRoleId(String roleId);

    /**
     * 通过父级IDS查找子集
     *
     * @param ids 父级IDS
     * @return List
     * @apiNote mysql8语法  其他数据库不支持
     */
    List<MenuEntity> findChildrenByParentIds(@Param("ids") List<String> ids);
}
