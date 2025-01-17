package com.smart.system.service;

import com.smart.entity.system.TenantEntity;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.TenantService;
import com.smart.system.dao.TenantDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户 ServiceImpl
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@Service("tenantService")
@Transactional(rollbackFor = Exception.class)
public class TenantServiceImpl extends BaseServiceImpl<TenantDao, TenantEntity> implements TenantService {
}