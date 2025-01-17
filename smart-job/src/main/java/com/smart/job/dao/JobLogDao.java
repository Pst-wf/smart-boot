package com.smart.job.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.mybatis.dao.BaseDao;
import com.smart.entity.job.JobLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 任务执行日志
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface JobLogDao extends BaseDao<JobLogEntity> {

    List<String> findLostJobIds(Date lostTime);

    List<String> findFailJobLogIds(@Param("pageSize") int pageSize);

    int updateAlarmStatus(@Param("logId") String logId,
                          @Param("oldAlarmStatus") int oldAlarmStatus,
                          @Param("newAlarmStatus") int newAlarmStatus);

    LinkedHashMap<String, Object> findLogReport(@Param("from") Date from,
                                                @Param("to") Date to);

    List<String> findClearLogIds(@Param("jobGroup") int jobGroup,
                                 @Param("jobId") int jobId,
                                 @Param("clearBeforeTime") Date clearBeforeTime,
                                 @Param("clearBeforeNum") int clearBeforeNum,
                                 @Param("pageSize") int pageSize);

    int clearLog(@Param("logIds") List<String> logIds);

    List<JobLogEntity> findList(@Param(Constants.WRAPPER) QueryWrapper<JobLogEntity> wrapper, @Param("log") JobLogEntity log);
}
