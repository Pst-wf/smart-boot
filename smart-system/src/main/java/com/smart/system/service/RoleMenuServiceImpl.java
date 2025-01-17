package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.RoleMenuDao;
import com.smart.entity.system.RoleMenuEntity;
import com.smart.service.system.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色-菜单 ServiceImpl
 *
 * @author wf
 * @since 2022-01-01 17:28:23
 */
@Service("roleMenuService")
@Transactional(rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends BaseServiceImpl<RoleMenuDao, RoleMenuEntity> implements RoleMenuService {

}