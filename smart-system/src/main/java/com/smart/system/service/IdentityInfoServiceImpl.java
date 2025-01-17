package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.IdentityDao;
import com.smart.entity.system.IdentityEntity;
import com.smart.service.system.IdentityInfoService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 身份 ServiceImpl
 *
 * @author wf
 * @since 2022-01-14 22:38:27
 */
@Service("identityInfoService")
@Transactional(rollbackFor = Exception.class)
public class IdentityInfoServiceImpl extends BaseServiceImpl<IdentityDao, IdentityEntity> implements IdentityInfoService {

    @Override
    public IdentityEntity getInfo(String id) {
        return baseMapper.getInfo(id);
    }

    /**
     * 获取缓存身份信息
     *
     * @param id 身份ID
     * @return IdentityEntity
     */
    @Override
    @Cacheable(cacheNames = "identity", key = "#id", unless = "#result == null")
    public IdentityEntity getCacheIdentity(String id) {
        return super.get(id);
    }
}