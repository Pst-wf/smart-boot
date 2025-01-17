package com.smart.job.service;

import com.smart.entity.job.JobGroupEntity;
import com.smart.job.dao.JobGroupDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.job.JobGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 执行器 ServiceImpl
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@Service("jobGroupService")
@Transactional(rollbackFor = Exception.class)
public class JobGroupServiceImpl extends BaseServiceImpl<JobGroupDao, JobGroupEntity> implements JobGroupService {

}