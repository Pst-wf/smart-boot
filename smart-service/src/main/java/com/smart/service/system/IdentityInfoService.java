package com.smart.service.system;

import com.smart.entity.system.IdentityEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 身份 Service
 *
 * @author wf
 * @since 2022-01-14 22:38:27
 */
public interface IdentityInfoService extends BaseService<IdentityEntity> {
    /**
     * 获取身份
     *
     * @param id 身份ID
     * @return IdentityEntity
     */
    IdentityEntity getInfo(String id);

    /**
     * 获取缓存身份信息
     *
     * @param id 身份ID
     * @return IdentityEntity
     */
    IdentityEntity getCacheIdentity(String id);
}

