package com.smart.job.tasks;

import com.smart.job.core.context.XxlJobHelper;
import com.smart.job.core.handler.annotation.XxlJob;
import com.smart.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 任务处理器
 *
 * @author wf
 * @since 2022-04-07 15:19:41
 */
@Component
public class HandleJob {
    private static final Logger logger = LoggerFactory.getLogger(HandleJob.class);

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        logger.info("=============================== 开始任务 {} ===============================", DateUtil.formatDate(new Date()));
        logger.info("本次执行参数-> {}", XxlJobHelper.getJobParam());
        // default success
        logger.info("=============================== 任务结束 {} ===============================", DateUtil.formatDate(new Date()));
    }
}
