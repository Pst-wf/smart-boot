package com.smart.job.core.executor;


import com.smart.job.core.biz.AdminBiz;
import com.smart.job.core.biz.client.AdminBizClient;
import com.smart.job.core.handler.IJobHandler;
import com.smart.job.core.handler.annotation.XxlJob;
import com.smart.job.core.handler.impl.MethodJobHandler;
import com.smart.job.core.log.XxlJobFileAppender;
import com.smart.job.core.server.EmbedServer;
import com.smart.job.core.thread.JobLogFileCleanThread;
import com.smart.job.core.thread.JobThread;
import com.smart.job.core.thread.TriggerCallbackThread;
import com.smart.job.core.util.IpUtil;
import com.smart.job.core.util.NetUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli
 * @since 2016/3/2 21:14.
 */
public class XxlJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    // ---------------------- param ----------------------

    private String adminAddresses;
    private String accessToken;
    private String appName;
    private String title;
    private String address;
    private String ip;
    private int port;
    private String logPath;
    private int logRetentionDays;

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }


    // ---------------------- start + stop ----------------------

    public void start() throws Exception {

        // init logPath
        XxlJobFileAppender.initLogPath(logPath);

        // init invoker, admin-client
        initAdminBizList(adminAddresses, accessToken);


        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);

        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server
        initEmbedServer(address, ip, port, appName, accessToken, title);
    }

    public void destroy() {
        // destroy executor-server
        stopEmbedServer();

        // destroy JOB_THREAD_REPOSITORY
        if (!JOB_THREAD_REPOSITORY.isEmpty()) {
            for (Map.Entry<String, JobThread> item : JOB_THREAD_REPOSITORY.entrySet()) {
                JobThread oldJobThread = removeJobThread(item.getKey(), "web container destroy and kill the job.");
                // wait for job thread push result to callback queue
                if (oldJobThread != null) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        logger.error(">>>>>>>>>>> xxl-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
            JOB_THREAD_REPOSITORY.clear();
        }
        JOB_HANDLER_REPOSITORY.clear();


        // destroy JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();

        // destroy TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();

    }


    // ---------------------- admin-client (rpc invoker) ----------------------

    @Getter
    private static List<AdminBiz> adminBizList;

    private void initAdminBizList(String adminAddresses, String accessToken) {
        if (adminAddresses != null && !adminAddresses.trim().isEmpty()) {
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && !address.trim().isEmpty()) {

                    AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);

                    if (adminBizList == null) {
                        adminBizList = new ArrayList<>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    // ---------------------- executor-server (rpc provider) ----------------------

    private EmbedServer embedServer = null;

    private void initEmbedServer(String address, String ip, int port, String appName, String accessToken, String title) {

        // fill ip port
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && !ip.trim().isEmpty()) ? ip : IpUtil.getIp();

        // generate address
        if (address == null || address.trim().isEmpty()) {
            // registry-address：default use address to registry , otherwise use ip:port if address is null
            String ipPortAddress = IpUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ipPortAddress);
        }

        // accessToken
        if (accessToken == null || accessToken.trim().isEmpty()) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken为空。为确保系统安全，请设置accessToken.");
        }

        // start
        embedServer = new EmbedServer();
        embedServer.start(address, port, appName, accessToken, title);
    }

    private void stopEmbedServer() {
        // stop provider factory
        if (embedServer != null) {
            try {
                embedServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    //---------------------- job handler repository ----------------------

    private static final ConcurrentMap<String, IJobHandler> JOB_HANDLER_REPOSITORY = new ConcurrentHashMap<>();

    public static IJobHandler loadJobHandler(String name) {
        return JOB_HANDLER_REPOSITORY.get(name);
    }

    public static void registJobHandler(String name, IJobHandler jobHandler) {
        logger.error(">>>>>>>>>>> 任务处理器[ {} ]注册成功!     Job Handler:{}", name, jobHandler);
        JOB_HANDLER_REPOSITORY.put(name, jobHandler);
    }

    protected void registJobHandler(XxlJob xxlJob, Object bean, Method executeMethod) {
        if (xxlJob == null) {
            return;
        }

        String name = xxlJob.value();
        //make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (name.trim().isEmpty()) {
            throw new RuntimeException("Job Handler方法名称无效, for[" + clazz + "#" + methodName + "] .");
        }
        if (loadJobHandler(name) != null) {
            throw new RuntimeException("Job Handler[" + name + "] 名称冲突.");
        }

        executeMethod.setAccessible(true);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (!xxlJob.init().trim().isEmpty()) {
            try {
                initMethod = clazz.getDeclaredMethod(xxlJob.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Job Handler 加载方法无效, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (!xxlJob.destroy().trim().isEmpty()) {
            try {
                destroyMethod = clazz.getDeclaredMethod(xxlJob.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Job Handler 销毁方法无效, for[" + clazz + "#" + methodName + "] .");
            }
        }

        // registry jobhandler
        registJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));

    }


    // ---------------------- job thread repository ----------------------

    private static final ConcurrentMap<String, JobThread> JOB_THREAD_REPOSITORY = new ConcurrentHashMap<>();

    public static JobThread registJobThread(String jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", jobId, handler);
        // putIfAbsent | oh my god, map's put method return the old value!!!
        JobThread oldJobThread = JOB_THREAD_REPOSITORY.put(jobId, newJobThread);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static JobThread removeJobThread(String jobId, String removeOldReason) {
        JobThread oldJobThread = JOB_THREAD_REPOSITORY.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();

            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(String jobId) {
        return JOB_THREAD_REPOSITORY.get(jobId);
    }

}
