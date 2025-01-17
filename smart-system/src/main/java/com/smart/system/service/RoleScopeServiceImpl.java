package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.RoleScopeDao;
import com.smart.entity.system.RoleScopeEntity;
import com.smart.service.system.RoleScopeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色-数据权限 ServiceImpl
 *
 * @author wf
 * @since 2022-01-23 18:13:52
 */
@Service("roleScopeService")
@Transactional(rollbackFor = Exception.class)
public class RoleScopeServiceImpl extends BaseServiceImpl<RoleScopeDao, RoleScopeEntity> implements RoleScopeService {

}