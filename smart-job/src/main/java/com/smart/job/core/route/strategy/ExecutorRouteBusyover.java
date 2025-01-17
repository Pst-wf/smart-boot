package com.smart.job.core.route.strategy;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smart.common.utils.StringUtil;
import com.smart.job.core.biz.ExecutorBiz;
import com.smart.job.core.biz.model.IdleBeatParam;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.biz.model.TriggerParam;
import com.smart.job.core.route.ExecutorRouter;
import com.smart.job.core.scheduler.XxlJobScheduler;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteBusyover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        JSONArray arr = new JSONArray();
        for (String address : addressList) {
            // beat
            ReturnT<String> idleBeatResult = null;
            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
                idleBeatResult = executorBiz.idleBeat(new IdleBeatParam(triggerParam.getJobId()));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                idleBeatResult = new ReturnT<String>(ReturnT.FAIL_CODE, "" + e);
            }
            JSONObject jo1 = new JSONObject(true);
            jo1.put("label", "address");
            jo1.put("value", address);

            JSONObject jo2 = new JSONObject(true);
            jo2.put("label", "code");
            jo2.put("value", idleBeatResult.getCode());

            JSONObject jo3 = new JSONObject(true);
            jo3.put("label", "msg");
            jo3.put("value", StringUtil.isNotBlank(idleBeatResult.getMsg()) ? idleBeatResult.getMsg() : "");
            arr.add(jo1);
            arr.add(jo2);
            arr.add(jo3);

            // beat success
            if (idleBeatResult.getCode() == ReturnT.SUCCESS_CODE) {
                idleBeatResult.setMsg(arr.toJSONString());
                idleBeatResult.setContent(address);
                return idleBeatResult;
            }
        }
        return new ReturnT<String>(ReturnT.FAIL_CODE, arr.toJSONString());
    }

}
