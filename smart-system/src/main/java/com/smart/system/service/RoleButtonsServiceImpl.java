package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.RoleButtonsDao;
import com.smart.entity.system.RoleButtonsEntity;
import com.smart.service.system.RoleButtonsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色-按钮 ServiceImpl
 *
 * @author wf
 * @since 2024-06-20 11:17:34
 */
@Service("roleButtonsService")
@Transactional(rollbackFor = Exception.class)
public class RoleButtonsServiceImpl extends BaseServiceImpl<RoleButtonsDao, RoleButtonsEntity> implements RoleButtonsService {

}