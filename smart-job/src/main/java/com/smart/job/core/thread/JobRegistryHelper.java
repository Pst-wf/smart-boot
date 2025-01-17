package com.smart.job.core.thread;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.common.utils.StringUtil;
import com.smart.job.core.biz.model.RegistryParam;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.enums.RegistryConfig;
import com.smart.entity.job.JobGroupEntity;
import com.smart.entity.job.JobRegistryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryHelper {
    private static Logger logger = LoggerFactory.getLogger(JobRegistryHelper.class);

    private static JobRegistryHelper instance = new JobRegistryHelper();

    public static JobRegistryHelper getInstance() {
        return instance;
    }

    private ThreadPoolExecutor registryOrRemoveThreadPool = null;
    private Thread registryMonitorThread;
    private volatile boolean toStop = false;

    public void start() {

        // for registry or remove
        registryOrRemoveThreadPool = new ThreadPoolExecutor(
                2,
                10,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobRegistryMonitorHelper-registryOrRemoveThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        logger.warn(">>>>>>>>>>> xxl-job, registry or remove too fast, match threadpool rejected handler(run now).");
                    }
                });

        // for monitor
        registryMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // auto registry group
                        List<JobGroupEntity> groupList = XxlJobAdminConfig.getAdminConfig().getJobGroupDao().findByAddressType(0);
                        if (groupList != null && !groupList.isEmpty()) {

                            // remove dead address (admin/executor)
                            List<String> ids = XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
                            if (ids != null && ids.size() > 0) {
                                XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().removeDead(ids);
                            }

                            // fresh online address (admin/executor)
                            HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
                            List<JobRegistryEntity> list = XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
                            if (list != null) {
                                for (JobRegistryEntity item : list) {
                                    if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                        String appname = item.getRegistryKey();
                                        List<String> registryList = appAddressMap.get(appname);
                                        if (registryList == null) {
                                            registryList = new ArrayList<String>();
                                        }

                                        if (!registryList.contains(item.getRegistryValue())) {
                                            registryList.add(item.getRegistryValue());
                                        }
                                        appAddressMap.put(appname, registryList);
                                    }
                                }
                            }

                            // fresh group address
                            for (JobGroupEntity group : groupList) {
                                List<String> registryList = appAddressMap.get(group.getAppName());
                                String addressListStr = null;
                                if (registryList != null && !registryList.isEmpty()) {
                                    Collections.sort(registryList);
                                    StringBuilder addressListSB = new StringBuilder();
                                    for (String item : registryList) {
                                        addressListSB.append(item).append(",");
                                    }
                                    addressListStr = addressListSB.toString();
                                    addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                                }
                                group.setAddressList(addressListStr);
                                group.setUpdateDate(new Date());

                                XxlJobAdminConfig.getAdminConfig().getJobGroupDao().updateById(group);
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
            }
        });
        registryMonitorThread.setDaemon(true);
        registryMonitorThread.setName("xxl-job, admin JobRegistryMonitorHelper-registryMonitorThread");
        registryMonitorThread.start();
    }

    public void toStop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        registryOrRemoveThreadPool.shutdownNow();

        // stop monitir (interrupt and wait)
        registryMonitorThread.interrupt();
        try {
            registryMonitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public ReturnT<String> registry(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }
        // async execute
        registryOrRemoveThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int ret = XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
                if (ret < 1) {
                    // 注册
                    JobRegistryEntity registry = new JobRegistryEntity();
                    registry.setRegistryKey(registryParam.getRegistryKey());
                    registry.setRegistryGroup(registryParam.getRegistryGroup());
                    registry.setRegistryValue(registryParam.getRegistryValue());
                    registry.setUpdateTime(new Date());

                    XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().insert(registry);

                    // fresh
                    freshGroupRegistryInfo(true, registryParam);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        // async execute
        registryOrRemoveThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int ret = XxlJobAdminConfig.getAdminConfig().getJobRegistryDao().registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                if (ret > 0) {
                    // fresh
                    freshGroupRegistryInfo(false, registryParam);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    private void freshGroupRegistryInfo(boolean edited, RegistryParam registryParam) {
        if (edited) {
            // Under consideration, prevent affecting core tables
            // 修改或新增
            JobGroupEntity groupEntity = XxlJobAdminConfig.getAdminConfig().getJobGroupDao().selectOne(new LambdaQueryWrapper<JobGroupEntity>().eq(JobGroupEntity::getAppName, registryParam.getRegistryKey()).eq(JobGroupEntity::getIsDeleted, "0"));
            if (groupEntity == null) {
                groupEntity = new JobGroupEntity();
            }
            String addressList = "";
            if (StringUtil.isNotBlank(registryParam.getRegistryValue())) {
                String[] arr = registryParam.getRegistryValue().split(",");
                List<String> formatList = new ArrayList<>();
                for (String address : arr) {
                    // valid
                    if (address.endsWith("/")) {
                        address = address.substring(0, address.length() - 1);
                        formatList.add(address);
                    }
                }
                addressList = formatList.stream().map(String::valueOf).collect(Collectors.joining(","));
            }
            groupEntity.setAddressList(addressList);
            groupEntity.setAppName(registryParam.getRegistryKey());
            groupEntity.setAddressType("0");
            groupEntity.setTitle(registryParam.getRegistryTitle());
            groupEntity.setOnlineStatus("1");
            XxlJobAdminConfig.getAdminConfig().getJobGroupService().saveOrUpdate(groupEntity);
        } else {
            // 删除
            XxlJobAdminConfig.getAdminConfig().getJobGroupDao().changeStatus("0", registryParam.getRegistryKey());
        }

    }


}
