package com.smart.job.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.smart.entity.job.JobEntity;
import com.smart.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Mapper
public interface JobDao extends BaseDao<JobEntity> {

    List<JobEntity> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pageSize") int pageSize);

    List<JobEntity> findPage(@Param(Constants.WRAPPER) QueryWrapper<JobEntity> wrapper, @Param("job") JobEntity job);
}
