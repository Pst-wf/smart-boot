package com.smart.job.service;

import com.smart.entity.job.JobLogReportEntity;
import com.smart.job.dao.JobLogReportDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.job.JobLogReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 调度日志 ServiceImpl
 *
 * @author wf
 * @since 2022-04-18 13:21:05
 */
@Service("jobLogReportService")
@Transactional(rollbackFor = Exception.class)
public class JobLogReportServiceImpl extends BaseServiceImpl<JobLogReportDao, JobLogReportEntity> implements JobLogReportService {

}