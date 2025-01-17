package com.smart.job.dao;

import com.smart.entity.job.JobLogReportEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调度日志
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface JobLogReportDao extends BaseDao<JobLogReportEntity> {

    int updateByTriggerDay(JobLogReportEntity xxlJobLogReport);

}
