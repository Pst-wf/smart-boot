package com.smart.job.core.thread;

import com.smart.entity.job.JobLogReportEntity;
import com.smart.job.core.conf.XxlJobAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {
    private static final Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);

    private static final JobLogReportHelper instance = new JobLogReportHelper();

    public static JobLogReportHelper getInstance() {
        return instance;
    }

    private Thread logThread;
    private volatile boolean toStop = false;

    public void start() {
        logThread = new Thread(() -> {

            // last clean log time
            long lastCleanLogTime = 0;


            while (!toStop) {

                // 1、log-report refresh: refresh log report in 3 days
                try {

                    for (int i = 0; i < 3; i++) {

                        // today
                        Calendar itemDay = Calendar.getInstance();
                        itemDay.add(Calendar.DAY_OF_MONTH, -i);
                        itemDay.set(Calendar.HOUR_OF_DAY, 0);
                        itemDay.set(Calendar.MINUTE, 0);
                        itemDay.set(Calendar.SECOND, 0);
                        itemDay.set(Calendar.MILLISECOND, 0);

                        Date todayFrom = itemDay.getTime();

                        itemDay.set(Calendar.HOUR_OF_DAY, 23);
                        itemDay.set(Calendar.MINUTE, 59);
                        itemDay.set(Calendar.SECOND, 59);
                        itemDay.set(Calendar.MILLISECOND, 999);

                        Date todayTo = itemDay.getTime();

                        // refresh log-report every minute
                        JobLogReportEntity xxlJobLogReport = new JobLogReportEntity();
                        xxlJobLogReport.setTriggerDay(todayFrom);
                        xxlJobLogReport.setRunningCount(0);
                        xxlJobLogReport.setSucCount(0);
                        xxlJobLogReport.setFailCount(0);

                        Map<String, Object> triggerCountMap = XxlJobAdminConfig.getAdminConfig().getJobLogDao().findLogReport(todayFrom, todayTo);
                        if (triggerCountMap != null && triggerCountMap.size() > 0) {
                            int triggerDayCount = triggerCountMap.containsKey("triggerDayCount") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCount"))) : 0;
                            int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))) : 0;
                            int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc") ? Integer.parseInt(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))) : 0;
                            int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                            xxlJobLogReport.setRunningCount(triggerDayCountRunning);
                            xxlJobLogReport.setSucCount(triggerDayCountSuc);
                            xxlJobLogReport.setFailCount(triggerDayCountFail);
                        }

                        // do refresh
                        int ret = XxlJobAdminConfig.getAdminConfig().getJobLogReportDao().updateByTriggerDay(xxlJobLogReport);
                        if (ret < 1) {
                            XxlJobAdminConfig.getAdminConfig().getJobLogReportDao().insert(xxlJobLogReport);
                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e);
                    }
                }

                // 2、log-clean: switch open & once each day
                if (XxlJobAdminConfig.getAdminConfig().getLogRetentionDays() > 0
                        && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                    // expire-time
                    Calendar expiredDay = Calendar.getInstance();
                    expiredDay.add(Calendar.DAY_OF_MONTH, -1 * XxlJobAdminConfig.getAdminConfig().getLogRetentionDays());
                    expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                    expiredDay.set(Calendar.MINUTE, 0);
                    expiredDay.set(Calendar.SECOND, 0);
                    expiredDay.set(Calendar.MILLISECOND, 0);
                    Date clearBeforeTime = expiredDay.getTime();

                    // clean expired log
                    List<String> logIds = null;
                    do {
                        logIds = XxlJobAdminConfig.getAdminConfig().getJobLogDao().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                        if (logIds != null && logIds.size() > 0) {
                            XxlJobAdminConfig.getAdminConfig().getJobLogDao().clearLog(logIds);
                        }
                    } while (logIds != null && logIds.size() > 0);

                    // update clean time
                    lastCleanLogTime = System.currentTimeMillis();
                }

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

            logger.info(">>>>>>>>>>> xxl-job, job log report thread stop");

        });
        logThread.setDaemon(true);
        logThread.setName("xxl-job, admin JobLogReportHelper");
        logThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        logThread.interrupt();
        try {
            logThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
