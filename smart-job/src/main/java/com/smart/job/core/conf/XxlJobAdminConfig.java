package com.smart.job.core.conf;

import com.smart.job.core.alarm.JobAlarmer;
import com.smart.job.core.scheduler.XxlJobScheduler;
import com.smart.job.dao.*;
import com.smart.service.job.JobGroupService;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class XxlJobAdminConfig implements InitializingBean, DisposableBean {

    @Getter
    private static XxlJobAdminConfig adminConfig = null;

    @Value("${job.openStatus}")
    private boolean openStatus;
    // ---------------------- XxlJobScheduler ----------------------

    private XxlJobScheduler xxlJobScheduler;

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        xxlJobScheduler = new XxlJobScheduler();
        if (openStatus) {
            // 初始化
            xxlJobScheduler.init();
        }
    }

    @Override
    public void destroy() throws Exception {
        xxlJobScheduler.destroy();
    }


    // ---------------------- XxlJobScheduler ----------------------

    @Value("${xxl.job.i18n}")
    private String i18n;

    @Getter
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${xxl.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

    @Value("${xxl.job.logRetentionDays}")
    private int logRetentionDays;

    // dao, service

    @Getter
    @Resource
    private JobLogDao jobLogDao;
    @Getter
    @Resource
    private JobDao jobDao;
    @Getter
    @Resource
    private JobRegistryDao jobRegistryDao;
    @Getter
    @Resource
    private JobGroupDao jobGroupDao;
    @Getter
    @Resource
    private JobLogReportDao jobLogReportDao;
    @Getter
    @Resource
    private JavaMailSender mailSender;
    @Getter
    @Resource
    private DataSource dataSource;
    @Getter
    @Resource
    private JobAlarmer jobAlarmer;
    @Getter
    @Resource
    private JobGroupService jobGroupService;

    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public int getLogRetentionDays() {
        if (logRetentionDays < 7) {
            // Limit greater than or equal to 7, otherwise close
            return -1;
        }
        return logRetentionDays;
    }

}
