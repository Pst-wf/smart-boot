package com.smart.job.service;

import com.smart.entity.job.JobRegistryEntity;
import com.smart.job.dao.JobRegistryDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.job.JobRegistryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务注册 ServiceImpl
 *
 * @author wf
 * @since 2022-04-18 13:05:27
 */
@Service("jobRegistryService")
@Transactional(rollbackFor = Exception.class)
public class JobRegistryServiceImpl extends BaseServiceImpl<JobRegistryDao, JobRegistryEntity> implements JobRegistryService {

}