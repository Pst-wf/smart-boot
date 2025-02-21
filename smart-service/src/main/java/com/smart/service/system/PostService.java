package com.smart.service.system;

import com.smart.entity.system.PostEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 岗位 Service
 *
 * @author wf
 * @since 2022-03-12 08:42:03
 */
public interface PostService extends BaseService<PostEntity> {
    /**
     * 启用/停用
     *
     * @param entity bean实体
     * @return boolean
     */
    boolean updateStatus(PostEntity entity);
}

