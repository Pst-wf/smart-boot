package com.smart.service.job;

import com.smart.entity.job.JobEntity;
import com.smart.mybatis.service.BaseService;

/**
 * 任务 Service
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
public interface JobService extends BaseService<JobEntity> {

    /**
     * 启动
     *
     * @param jobEntity Job实体
     * @return boolean
     */
    boolean start(JobEntity jobEntity);

    /**
     * 停止
     *
     * @param jobEntity Job实体
     * @return boolean
     */
    boolean stop(JobEntity jobEntity);
}

