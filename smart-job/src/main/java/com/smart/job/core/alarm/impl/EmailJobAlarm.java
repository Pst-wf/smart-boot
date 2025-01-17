package com.smart.job.core.alarm.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.StringUtil;
import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobGroupEntity;
import com.smart.entity.job.JobLogEntity;
import com.smart.job.core.alarm.JobAlarm;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.util.I18nUtil;
import com.smart.service.system.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * job alarm by email
 *
 * @author xuxueli 2020-01-19
 */
@Component
public class EmailJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(EmailJobAlarm.class);
    @Autowired
    ConfigService configService;

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(JobEntity info, JobLogEntity jobLog) {
        boolean alarmResult = true;

        // send monitor email
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() == null || jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>TriggerMsg=<br>" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() == null || jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>HandleCode=" + jobLog.getHandleMsg();
            }

            // email info
            JobGroupEntity group = XxlJobAdminConfig.getAdminConfig().getJobGroupDao().selectById(info.getJobGroup());
            String personal = I18nUtil.getString("admin_name_full");
            String title = I18nUtil.getString("jobconf_monitor");
            String content = MessageFormat.format(loadEmailJobAlarmTemplate(),
                    group != null ? group.getTitle() : "null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);

            Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
            for (String email : emailSet) {

                // make mail
                try {
                    MimeMessage mimeMessage = XxlJobAdminConfig.getAdminConfig().getMailSender().createMimeMessage();

                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                    String emailConfig = configService.getConfig("sys_email_config");
                    if (StringUtil.isNotBlank(emailConfig)) {
                        JSONObject jsonObject = JSON.parseObject(emailConfig);
                        helper.setFrom(jsonObject.getString("from"), personal);
                        helper.setTo(email);
                        helper.setSubject(title);
                        helper.setText(content, true);
                        XxlJobAdminConfig.getAdminConfig().getMailSender().send(mimeMessage);
                    } else {
                        logger.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId());
                        alarmResult = false;
                    }
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);

                    alarmResult = false;
                }

            }
        }

        return alarmResult;
    }

    /**
     * load email job alarm template
     *
     * @return
     */
    private static final String loadEmailJobAlarmTemplate() {
        String mailBodyTemplate = "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobgroup") + "</td>\n" +
                "         <td width=\"10%\" >" + I18nUtil.getString("jobinfo_field_id") + "</td>\n" +
                "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobdesc") + "</td>\n" +
                "         <td width=\"10%\" >" + I18nUtil.getString("jobconf_monitor_alarm_title") + "</td>\n" +
                "         <td width=\"40%\" >" + I18nUtil.getString("jobconf_monitor_alarm_content") + "</td>\n" +
                "      </tr>\n" +
                "   </thead>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{0}</td>\n" +
                "         <td>{1}</td>\n" +
                "         <td>{2}</td>\n" +
                "         <td>" + I18nUtil.getString("jobconf_monitor_alarm_type") + "</td>\n" +
                "         <td>{3}</td>\n" +
                "      </tr>\n" +
                "   </tbody>\n" +
                "</table>";

        return mailBodyTemplate;
    }

}
