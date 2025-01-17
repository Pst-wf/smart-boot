package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.LoginLogDao;
import com.smart.entity.system.LoginLogEntity;
import com.smart.service.system.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录日志 ServiceImpl
 *
 * @author wf
 * @since 2022-01-30 22:59:36
 */
@Service("loginLogService")
@Transactional(rollbackFor = Exception.class)
public class LoginLogServiceImpl extends BaseServiceImpl<LoginLogDao, LoginLogEntity> implements LoginLogService {

}