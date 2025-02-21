package com.smart.service.system;

import com.smart.entity.system.DeptEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 机构 Service
 *
 * @author wf
 * @since 2022-01-15 00:21:17
 */
public interface DeptService extends BaseService<DeptEntity> {
    /**
     * 获取导出数据
     *
     * @param deptEntity 机构bean
     * @return list
     */
    List<DeptEntity> findExportList(DeptEntity deptEntity);

    /**
     * 获取缓存部门
     *
     * @param id 部门ID
     * @return DeptEntity
     */
    DeptEntity getCacheDept(String id);

    /**
     * 通过IDS获取部门集合
     *
     * @param ids 主键IDS
     * @return List
     */
    List<DeptEntity> getDeptListByIds(String ids);

    /**
     * 通过部门ID获取部门所属的所有父级名称
     *
     * @param deptId 部门ID
     * @return String
     */
    String getAncestorsDeptName(String deptId);

    /**
     * 获取树形结构
     *
     * @param deptEntity 部门bean
     * @return List
     */
    List<DeptEntity> getTree(DeptEntity deptEntity);

    /**
     * 获取树形结构 (MAP)
     *
     * @param deptEntity 部门bean
     * @return List
     */
    List<Map<String, Object>> getTreeMap(DeptEntity deptEntity);

    /**
     * 获取树形格式化结构
     *
     * @param deptEntity bean
     * @return List
     */
    List<Map<String, Object>> getTreeFormat(DeptEntity deptEntity);

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    boolean updateStatus(DeptEntity entity);
}

