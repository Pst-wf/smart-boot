package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.ButtonsDao;
import com.smart.entity.system.ButtonsEntity;
import com.smart.service.system.ButtonsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 按钮表 ServiceImpl
 *
 * @author wf
 * @since 2024-06-16 21:16:44
 */
@Service("buttonsService")
@Transactional(rollbackFor = Exception.class)
public class ButtonsServiceImpl extends BaseServiceImpl<ButtonsDao, ButtonsEntity> implements ButtonsService {
    /**
     * 根据角色ID获取按钮
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    public List<ButtonsEntity> findButtonsByRoleId(String roleId) {
        return baseMapper.findButtonsByRoleId(roleId);
    }
}