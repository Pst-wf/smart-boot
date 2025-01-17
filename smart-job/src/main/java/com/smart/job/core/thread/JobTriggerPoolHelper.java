package com.smart.job.core.thread;

import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.trigger.TriggerTypeEnum;
import com.smart.job.core.trigger.XxlJobTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * job trigger thread pool helper
 *
 * @author xuxueli 2018-07-03 21:08:07
 */
public class JobTriggerPoolHelper {
    private static Logger logger = LoggerFactory.getLogger(JobTriggerPoolHelper.class);


    // ---------------------- trigger pool ----------------------

    // fast/slow thread pool
    private ThreadPoolExecutor fastTriggerPool = null;
    private ThreadPoolExecutor slowTriggerPool = null;

    public void start() {
        fastTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode());
                    }
                });

        slowTriggerPool = new ThreadPoolExecutor(
                10,
                XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode());
                    }
                });
    }


    public void stop() {
        //triggerPool.shutdown();
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
        logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }


    /**
     * job timeout count
     *
     * @apiNote ms > min
     */
    private volatile long MIN_TIM = System.currentTimeMillis() / 60000;
    private final ConcurrentMap<String, AtomicInteger> JOB_TIMEOUT_COUNT_MAP = new ConcurrentHashMap<>();


    /**
     * add trigger
     */
    public void addTrigger(final String jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {
        // choose thread pool
        ThreadPoolExecutor triggerPool = fastTriggerPool;
        AtomicInteger jobTimeoutCount = JOB_TIMEOUT_COUNT_MAP.get(jobId);
        // job-timeout 10 times in 1 min
        if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {
            triggerPool = slowTriggerPool;
        }
        // trigger
        triggerPool.execute(() -> {
            long start = System.currentTimeMillis();
            try {
                // do trigger
                XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {

                // check timeout-count-map
                long minTimNow = System.currentTimeMillis() / 60000;
                if (MIN_TIM != minTimNow) {
                    MIN_TIM = minTimNow;
                    JOB_TIMEOUT_COUNT_MAP.clear();
                }

                // incr timeout-count-map
                long cost = System.currentTimeMillis() - start;
                // ob-timeout threshold 500ms
                if (cost > 500) {
                    AtomicInteger timeoutCount = JOB_TIMEOUT_COUNT_MAP.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }
            }
        });
    }

    // ---------------------- helper ----------------------
    private static JobTriggerPoolHelper HELPER = new JobTriggerPoolHelper();

    public static void toStart() {
        HELPER.start();
    }

    public static void toStop() {
        HELPER.stop();
    }

    /**
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     */
    public static void trigger(String jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        HELPER.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }

}
