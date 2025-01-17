package com.smart.system.service;

import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.system.dao.LogDao;
import com.smart.entity.system.LogEntity;
import com.smart.service.system.LogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志 ServiceImpl
 *
 * @author wf
 * @since 2022-01-30 22:59:36
 */
@Service("logService")
@Transactional(rollbackFor = Exception.class)
public class LogServiceImpl extends BaseServiceImpl<LogDao, LogEntity> implements LogService {

}