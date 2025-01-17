package com.smart.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.system.DeptEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机构
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface DeptDao extends BaseDao<DeptEntity> {

    /**
     * 获取所属公司IDS
     *
     * @param ancestors 部门序列
     * @return List
     */
    List<String> getOrganizationIds(String ancestors);

    /**
     * 列表（不分页）
     *
     * @param wrapper 查询条件
     * @return List
     */
    List<DeptEntity> findList(@Param(Constants.WRAPPER) QueryWrapper<DeptEntity> wrapper);

    /**
     * 通过父级IDS查找子集
     *
     * @param ids 父级IDS
     * @return List
     */
    List<DeptEntity> findChildrenByParentIds(@Param("ids") List<String> ids);
}
