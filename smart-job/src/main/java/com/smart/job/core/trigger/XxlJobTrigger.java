package com.smart.job.core.trigger;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.StringUtil;
import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobGroupEntity;
import com.smart.entity.job.JobLogEntity;
import com.smart.job.core.biz.ExecutorBiz;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.biz.model.TriggerParam;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.enums.ExecutorBlockStrategyEnum;
import com.smart.job.core.route.ExecutorRouteStrategyEnum;
import com.smart.job.core.scheduler.XxlJobScheduler;
import com.smart.job.core.util.I18nUtil;
import com.smart.job.core.util.IpUtil;
import com.smart.job.core.util.ThrowableUtil;
import com.smart.job.dao.JobLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;

/**
 * xxl-job trigger
 *
 * @author xxl
 * @since 2017/7/13.
 */
public class XxlJobTrigger {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    /**
     * trigger job
     *
     * @param jobId                 主键ID
     * @param triggerType           触发类型
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     * @param addressList           null: use executor addressList
     *                              not null: cover
     */
    public static void trigger(String jobId,
                               TriggerTypeEnum triggerType,
                               int failRetryCount,
                               String executorShardingParam,
                               String executorParam,
                               String addressList) {

        // load data
        JobEntity jobInfo = XxlJobAdminConfig.getAdminConfig().getJobDao().selectById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        JobGroupEntity group = XxlJobAdminConfig.getAdminConfig().getJobGroupDao().selectById(jobInfo.getJobGroup());

        // cover addressList
        if (addressList != null && !addressList.trim().isEmpty()) {
            group.setAddressType("1");
            group.setAddressList(addressList.trim());
        }

        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.parseInt(shardingArr[0]);
                shardingParam[1] = Integer.parseInt(shardingArr[1]);
            }
        }
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
                && group.getRegistryList() != null && !group.getRegistryList().isEmpty()
                && shardingParam == null) {
            for (int i = 0; i < group.getRegistryList().size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }

    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private static void processTrigger(JobGroupEntity group, JobEntity jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        // param
        // block strategy
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        // route strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;
        JobLogDao jobLogDao = XxlJobAdminConfig.getAdminConfig().getJobLogDao();

        // 1、save log-id
        JobLogEntity jobLog = new JobLogEntity();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        jobLogDao.insert(jobLog);
        logger.info(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdateTime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        JSONArray arr1 = new JSONArray();
        JSONObject jo1 = new JSONObject(true);
        jo1.put("label", I18nUtil.getString("jobconf_trigger_type"));
        jo1.put("value", triggerType.getTitle());

        JSONObject jo2 = new JSONObject(true);
        jo2.put("label", I18nUtil.getString("jobconf_trigger_admin_address"));
        jo2.put("value", IpUtil.getIp());

        JSONObject jo3 = new JSONObject(true);
        jo3.put("label", I18nUtil.getString("jobconf_trigger_exe_regtype"));
        jo3.put("value", (Objects.equals(group.getAddressType(), "0")) ? I18nUtil.getString("jobgroup_field_addressType_0") : I18nUtil.getString("jobgroup_field_addressType_1"));

        JSONObject jo4 = new JSONObject(true);
        jo4.put("label", I18nUtil.getString("jobconf_trigger_exe_regaddress"));
        jo4.put("value", group.getRegistryList());

        JSONObject jo5 = new JSONObject(true);
        jo5.put("label", I18nUtil.getString("jobinfo_field_executorRouteStrategy"));
        jo5.put("value", executorRouteStrategyEnum.getTitle());

        arr1.add(jo1);
        arr1.add(jo2);
        arr1.add(jo3);
        arr1.add(jo4);
        arr1.add(jo5);

        if (shardingParam != null) {
            JSONObject jo6 = new JSONObject(true);
            jo6.put("label", "");
            jo6.put("value", shardingParam);
            arr1.add(jo6);
        }

        JSONObject jo7 = new JSONObject(true);
        jo7.put("label", I18nUtil.getString("jobinfo_field_executorBlockStrategy"));
        jo7.put("value", blockStrategy.getTitle());

        JSONObject jo8 = new JSONObject(true);
        jo8.put("label", I18nUtil.getString("jobinfo_field_timeout"));
        jo8.put("value", jobInfo.getExecutorTimeout() != null ? jobInfo.getExecutorTimeout().toString() : "");

        JSONObject jo9 = new JSONObject(true);
        jo9.put("label", I18nUtil.getString("jobinfo_field_executorFailRetryCount"));
        jo9.put("value", finalFailRetryCount + "");


        arr1.add(jo7);
        arr1.add(jo8);
        arr1.add(jo9);

        JSONArray arr2 = new JSONArray();
        if (routeAddressResult != null && routeAddressResult.getMsg() != null) {
            String msg = routeAddressResult.getMsg();
            JSONArray array = JSONArray.parseArray(msg);
            arr2.addAll(array);
        }
        if (triggerResult.getMsg() != null) {
            String msg = triggerResult.getMsg();
            JSONArray array = JSONArray.parseArray(msg);
            arr2.addAll(array);
        }
        JSONObject result = new JSONObject(true);
        result.put("arr1", arr1);
        result.put("arr2", arr2);
        // 6、save log trigger-info
        logger.info(">>>>>>>>>>> xxl-job update log , jobId:{}", jobLog.getId());
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(result.toJSONString());
        jobLogDao.updateById(jobLog);
        logger.info(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam 执行参数
     * @param address      地址
     * @return ReturnT
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        JSONArray arr = new JSONArray();
        JSONObject jo1 = new JSONObject(true);
        jo1.put("label", "address");
        jo1.put("value", address);

        JSONObject jo2 = new JSONObject(true);
        jo2.put("label", "code");
        jo2.put("value", runResult.getCode());

        JSONObject jo3 = new JSONObject(true);
        jo3.put("label", "msg");
        jo3.put("value", StringUtil.isNotBlank(runResult.getMsg()) ? runResult.getMsg() : "");
        arr.add(jo1);
        arr.add(jo2);
        arr.add(jo3);

        runResult.setMsg(arr.toJSONString());
        return runResult;
    }

}
