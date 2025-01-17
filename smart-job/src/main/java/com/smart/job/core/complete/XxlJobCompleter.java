package com.smart.job.core.complete;

import com.smart.common.utils.StringUtil;
import com.smart.entity.job.JobEntity;
import com.smart.entity.job.JobLogEntity;
import com.smart.job.core.biz.model.ReturnT;
import com.smart.job.core.conf.XxlJobAdminConfig;
import com.smart.job.core.context.XxlJobContext;
import com.smart.job.core.thread.JobTriggerPoolHelper;
import com.smart.job.core.trigger.TriggerTypeEnum;
import com.smart.job.core.util.I18nUtil;

import java.text.MessageFormat;

/**
 * @author xuxueli 2020-10-30 20:43:10
 */
public class XxlJobCompleter {

    /**
     * common fresh handle entrance (limit only once)
     *
     * @param log 日志
     */
    public static void updateHandleInfoAndFinish(JobLogEntity log) {

        // finish
        finishJob(log);

        // text最大64kb 避免长度过长
        if (log.getHandleMsg().length() > 15000) {
            log.setHandleMsg(log.getHandleMsg().substring(0, 15000));
        }

        // fresh handle
        XxlJobAdminConfig.getAdminConfig().getJobLogDao().updateById(log);
    }


    /**
     * do something to finish job
     */
    private static void finishJob(JobLogEntity log) {

        // 1、handle success, to trigger child job
        StringBuilder triggerChildMsg = null;
        if (log.getHandleCode() != null && XxlJobContext.HANDLE_CODE_SUCCESS == log.getHandleCode()) {
            JobEntity job = XxlJobAdminConfig.getAdminConfig().getJobDao().selectById(log.getJobId());
            if (job != null && job.getChildJobId() != null && !job.getChildJobId().trim().isEmpty()) {
                triggerChildMsg = new StringBuilder("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_child_run") + "<<<<<<<<<<< </span><br>");

                String[] childJobIds = job.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    String childJobId = (childJobIds[i] != null && !childJobIds[i].trim().isEmpty() && isNumeric(childJobIds[i])) ? childJobIds[i] : "";
                    if (StringUtil.isNotBlank(childJobId)) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        triggerChildMsg.append(MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? I18nUtil.getString("system_success") : I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg()));
                    } else {
                        triggerChildMsg.append(MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i]));
                    }
                }

            }
        }

        if (triggerChildMsg != null) {
            log.setHandleMsg(log.getHandleMsg() + triggerChildMsg);
        }

        // 2、fix_delay trigger next
        // on the way

    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
