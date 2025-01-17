package com.smart.job.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.smart.job.core.cron.CronExpression;
import com.smart.job.core.enums.ExecutorBlockStrategyEnum;
import com.smart.job.core.glue.GlueTypeEnum;
import com.smart.job.core.route.ExecutorRouteStrategyEnum;
import com.smart.job.core.scheduler.MisfireStrategyEnum;
import com.smart.job.core.scheduler.ScheduleTypeEnum;
import com.smart.job.core.thread.JobScheduleHelper;
import com.smart.job.core.util.I18nUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobGroupEntity;
import com.smart.job.dao.JobDao;
import com.smart.job.dao.JobGroupDao;
import com.smart.model.exception.SmartException;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.job.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 任务 ServiceImpl
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@Service("jobService")
@Transactional(rollbackFor = Exception.class)
public class JobServiceImpl extends BaseServiceImpl<JobDao, JobEntity> implements JobService {
    @Resource
    JobGroupDao jobGroupDao;

    @Override
    public Page<JobEntity> findPage(JobEntity entity) {
        QueryWrapper<JobEntity> wrapper = new QueryWrapper<>();
        wrapper = initWrapper(entity, wrapper);
        wrapper.eq("a.is_deleted", "0");
        wrapper.eq("b.is_deleted", "0");
        Page<JobEntity> page = PageHelper.startPage(entity.getCurrent(), entity.getSize());
        //根据条件查询
        baseMapper.findPage(wrapper, entity);
        return page;
    }

    /**
     * 保存之前处理
     *
     * @param entity bean 实体
     * @param isAdd  是否新增
     */
    @Override
    public void beforeSaveOrUpdate(JobEntity entity, boolean isAdd) {

        // valid base
        JobGroupEntity group = jobGroupDao.selectById(entity.getJobGroup());
        if (group == null) {
            throw new SmartException("执行器不存在");
        }
        if (entity.getJobDesc() == null || entity.getJobDesc().trim().length() == 0) {
            throw new SmartException("任务描述不能为空");
        }

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(entity.getScheduleType(), null);
        if (scheduleTypeEnum == null) {
            throw new SmartException("调度类型错误");
        }
        if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
            if (entity.getScheduleConf() == null || !CronExpression.isValidExpression(entity.getScheduleConf())) {
                throw new SmartException("Cron表达式错误");
            }
        } else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE/* || scheduleTypeEnum == ScheduleTypeEnum.FIX_DELAY*/) {
            if (entity.getScheduleConf() == null) {
                throw new SmartException("调度配置错误");
            }
            try {
                int fixSecond = Integer.parseInt(entity.getScheduleConf());
                if (fixSecond < 1) {
                    throw new SmartException("调度配置非法");
                }
            } catch (Exception e) {
                throw new SmartException("调度配置非法");
            }
        }

        // valid job
        if (GlueTypeEnum.match(entity.getGlueType()) == null) {
            throw new SmartException("运行模式错误");
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(entity.getGlueType()) && StringUtil.isBlank(entity.getExecutorHandler())) {
            throw new SmartException("执行任务不能为空");
        }
        // 》fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(entity.getGlueType()) && entity.getGlueSource() != null) {
            entity.setGlueSource(entity.getGlueSource().replaceAll("\r", ""));
        }

        // valid advanced
        if (ExecutorRouteStrategyEnum.match(entity.getExecutorRouteStrategy(), null) == null) {
            throw new SmartException("路由策略非法");
        }
        if (MisfireStrategyEnum.match(entity.getMisfireStrategy(), null) == null) {
            throw new SmartException("调度过期策略非法");
        }
        if (ExecutorBlockStrategyEnum.match(entity.getExecutorBlockStrategy(), null) == null) {
            throw new SmartException("阻塞处理策略非法");
        }

        // 》ChildJobId valid
        if (StringUtil.isNotBlank(entity.getChildJobId())) {
            String[] childJobIds = entity.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (StringUtil.isNotBlank(childJobIdItem)) {
                    JobEntity childJobInfo = baseMapper.selectById(childJobIdItem);
                    if (childJobInfo == null) {
                        throw new SmartException("子任务不存在");
                    }
                } else {
                    throw new SmartException("子任务非法");
                }
            }

            // join , avoid "xxx,,"
            StringBuilder temp = new StringBuilder();
            for (String item : childJobIds) {
                temp.append(item).append(",");
            }
            temp = new StringBuilder(temp.substring(0, temp.length() - 1));
            entity.setChildJobId(temp.toString());
        }

        // add in db
        entity.setGlueUpdateTime(new Date());
    }

    @Override
    public boolean start(JobEntity jobEntity) {
        JobEntity job = super.get(jobEntity.getId());

        // valid
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(job.getScheduleType(), ScheduleTypeEnum.NONE);
        if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
            throw new SmartException(I18nUtil.getString("schedule_type_none_limit_start"));
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = 0;
        try {
            Date nextValidTime = JobScheduleHelper.generateNextValidTime(job, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
            if (nextValidTime == null) {
                throw new SmartException(I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid"));
            }
            nextTriggerTime = nextValidTime.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SmartException(I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid"));
        }

        job.setTriggerStatus(1);
        job.setTriggerLastTime(0L);
        job.setTriggerNextTime(nextTriggerTime);

        JobEntity result = super.updateEntity(job);
        return result != null;
    }

    @Override
    public boolean stop(JobEntity jobEntity) {
        jobEntity.setTriggerStatus(0);
        jobEntity.setTriggerLastTime(0L);
        jobEntity.setTriggerNextTime(0L);
        JobEntity result = super.updateEntity(jobEntity);
        return result != null;
    }
}