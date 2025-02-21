package com.smart.service.system;

import com.smart.entity.system.RegionEntity;
import com.smart.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 行政区域 Service
 *
 * @author wf
 * @since 2025-02-20 16:59:05
 */
public interface RegionService extends BaseService<RegionEntity> {
    /**
     * 异步加载
     */
    List<RegionEntity> asyncLoading(RegionEntity regionEntity);

    /**
     * 树形结构
     */
    List<RegionEntity> tree(RegionEntity regionEntity);

    /**
     * 获取树形格式化结构
     *
     * @param regionEntity bean
     * @return List
     */
    List<Map<String, Object>> getTreeFormat(RegionEntity regionEntity);

    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    boolean updateStatus(RegionEntity entity);
}

