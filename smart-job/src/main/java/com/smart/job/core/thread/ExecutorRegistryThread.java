package com.smart.job.core.thread;

import com.smart.job.core.biz.AdminBiz;
import com.smart.job.core.biz.model.RegistryParam;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.enums.RegistryConfig;
import com.smart.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xxl
 * @since 2017/3/2.
 */
public class ExecutorRegistryThread {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private static final ExecutorRegistryThread INSTANCE = new ExecutorRegistryThread();

    public static ExecutorRegistryThread getInstance() {
        return INSTANCE;
    }

    private Thread registryThread;
    private volatile boolean toStop = false;

    public void start(final String appName, final String address, final String title) {

        // valid
        if (appName == null || appName.trim().isEmpty()) {
            logger.warn(">>>>>>>>>>> xxl-job, 执行器注册表配置失败, appName 为空.");
            return;
        }
        if (XxlJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> xxl-job, 执行器注册表配置失败, adminAddresses 为空.");
            return;
        }

        registryThread = new Thread(() -> {
            // registry
            while (!toStop) {
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appName, address, title);
                    for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                        try {
                            ReturnT<String> registryResult = adminBiz.registry(registryParam);
                            if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                                registryResult = ReturnT.SUCCESS;
                                logger.debug(">>>>>>>>>>> xxl-job, 任务注册成功, registryParam:{}, registryResult:{}", registryParam, registryResult);
                                break;
                            } else {
                                logger.info(">>>>>>>>>>> xxl-job, 任务注册失败, registryParam:{}, registryResult:{}", registryParam, registryResult);
                            }
                        } catch (Exception e) {
                            logger.info(">>>>>>>>>>> xxl-job, 任务注册异常, registryParam:{}", registryParam, e);
                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }

                }

                try {
                    if (!toStop) {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.warn(">>>>>>>>>>> xxl-job, 执行器注册表线程被中断, error msg:{}", e.getMessage());
                    }
                }
            }

            // registry remove
            try {
                RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appName, address, title);
                for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
                    try {
                        ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                        if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                            registryResult = ReturnT.SUCCESS;
                            logger.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                            break;
                        } else {
                            logger.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.info(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}", registryParam, e);
                        }

                    }

                }
            } catch (Exception e) {
                if (!toStop) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info(">>>>>>>>>>> xxl-job, executor registry thread destroy.");

        });
        registryThread.setDaemon(true);
        registryThread.setName("xxl-job, executor ExecutorRegistryThread");
        registryThread.start();
    }

    public void toStop() {
        toStop = true;

        // interrupt and wait
        if (registryThread != null) {
            registryThread.interrupt();
            try {
                registryThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

}
