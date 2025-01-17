package com.smart.job.core.alarm;

import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wf
 */
@Component
@Slf4j
public class JobAlarmer implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private List<JobAlarm> jobAlarmList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JobAlarm> serviceBeanMap = applicationContext.getBeansOfType(JobAlarm.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            jobAlarmList = new ArrayList<>(serviceBeanMap.values());
        }
    }

    /**
     * job alarm
     *
     * @param job    任务
     * @param jobLog 日志
     * @return boolean
     */
    public boolean alarm(JobEntity job, JobLogEntity jobLog) {

        boolean result = false;
        if (jobAlarmList != null && !jobAlarmList.isEmpty()) {
            // success means all-success
            result = true;
            for (JobAlarm alarm : jobAlarmList) {
                boolean resultItem = false;
                try {
                    resultItem = alarm.doAlarm(job, jobLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                if (!resultItem) {
                    result = false;
                }
            }
        }

        return result;
    }

}
