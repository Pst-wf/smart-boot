package com.smart.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.entity.system.RoleScopeEntity;
import com.smart.entity.system.ScopeEntity;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.system.RoleScopeService;
import com.smart.service.system.ScopeService;
import com.smart.system.dao.ScopeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据权限 ServiceImpl
 *
 * @author wf
 * @since 2022-01-23 18:13:52
 */
@Service("scopeService")
@Transactional(rollbackFor = Exception.class)
public class ScopeServiceImpl extends BaseServiceImpl<ScopeDao, ScopeEntity> implements ScopeService {
    @Autowired
    private RoleScopeService roleScopeService;


    @Override
    public boolean delete(ScopeEntity entity) {
        boolean b = super.delete(entity);
        if (b) {
            roleScopeService.remove(new LambdaQueryWrapper<RoleScopeEntity>().in(RoleScopeEntity::getScopeId, entity.getDeleteIds()));
        }
        return b;
    }

    /**
     * 根据角色ID获取数据权限
     *
     * @param roleId 角色ID
     * @return List
     */
    public List<ScopeEntity> findScopesByRoleId(String roleId) {
        return baseMapper.findScopesByRoleId(roleId);
    }
}