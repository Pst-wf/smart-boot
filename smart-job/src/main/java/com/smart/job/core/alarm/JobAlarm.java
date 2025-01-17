package com.smart.job.core.alarm;


import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobLogEntity;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * job alarm
     *
     * @param job 任务
     * @param log 日志
     * @return boolean
     */
    boolean doAlarm(JobEntity job, JobLogEntity log);

}
