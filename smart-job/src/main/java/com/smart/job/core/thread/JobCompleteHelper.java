package com.smart.job.core.thread;

import com.smart.entity.job.JobLogEntity;
import com.smart.job.core.biz.model.HandleCallbackParam;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.complete.XxlJobCompleter;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.util.DateUtil;
import com.smart.job.core.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * job lose-monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobCompleteHelper {
    private static Logger logger = LoggerFactory.getLogger(JobCompleteHelper.class);

    private static JobCompleteHelper instance = new JobCompleteHelper();

    public static JobCompleteHelper getInstance() {
        return instance;
    }

    // ---------------------- monitor ----------------------

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {

        // for callback
        callbackThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobLostMonitorHelper-callbackThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        logger.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                    }
                });


        // for monitor
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // wait for JobTriggerPoolHelper-init
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

                // monitor
                while (!toStop) {
                    try {
                        // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                        Date lostTime = DateUtil.addMinutes(new Date(), -10);
                        List<String> lostJobIds = XxlJobAdminConfig.getAdminConfig().getJobLogDao().findLostJobIds(lostTime);

                        if (lostJobIds != null && lostJobIds.size() > 0) {
                            for (String logId : lostJobIds) {
                                JobLogEntity log = new JobLogEntity();
                                log.setId(logId);
                                log.setHandleTime(new Date());
                                log.setHandleCode(ReturnT.FAIL_CODE);
                                log.setHandleMsg(I18nUtil.getString("joblog_lost_fail"));
                                XxlJobCompleter.updateHandleInfoAndFinish(log);
                            }

                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>> xxl-job, JobLostMonitorHelper stop");

            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobLostMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        callbackThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (HandleCallbackParam handleCallbackParam : callbackParamList) {
                    ReturnT<String> callbackResult = callback(handleCallbackParam);
                    logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                            (callbackResult.getCode() == ReturnT.SUCCESS_CODE ? "success" : "fail"), handleCallbackParam, callbackResult);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLogEntity log = XxlJobAdminConfig.getAdminConfig().getJobLogDao().selectById(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() != null && log.getHandleCode() != ReturnT.SUCCESS_CODE) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "log repeat callback.");     // avoid repeat callback, trigger child job etc
        }

        // handle msg
        StringBuilder handleMsg = new StringBuilder();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        XxlJobCompleter.updateHandleInfoAndFinish(log);

        return ReturnT.SUCCESS;
    }


}
