package com.smart.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.entity.job.JobLogEntity;
import com.smart.job.dao.JobLogDao;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.job.JobLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务执行日志 ServiceImpl
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@Service("jobLogService")
@Transactional(rollbackFor = Exception.class)
public class JobLogServiceImpl extends BaseServiceImpl<JobLogDao, JobLogEntity> implements JobLogService {

    @Override
    public Page<JobLogEntity> findPage(JobLogEntity entity) {
        QueryWrapper<JobLogEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper.eq("b.is_deleted", "0");
        wrapper.eq("c.is_deleted", "0");
        Page<JobLogEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        //根据条件查询
        baseMapper.findList(wrapper, entity);
        return page;
    }
}