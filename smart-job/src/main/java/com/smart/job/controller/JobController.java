package com.smart.job.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.job.JobEntity;
import com.smart.job.core.thread.JobTriggerPoolHelper;
import com.smart.job.core.trigger.TriggerTypeEnum;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.job.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 任务 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/job/job")
public class JobController {
    @Autowired
    JobService jobService;

    /**
     * 列表
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @GetMapping("/page")
    public String page(JobEntity jobEntity) {
        return Result.data(jobService.findPage(jobEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        JobEntity result = jobService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @HasPermission("job:add")
    @PostMapping("/save")
    @SaveLog(module = "任务管理", type = LogType.ADD)
    public String save(@RequestBody JobEntity jobEntity) {
        JobEntity result = jobService.saveEntity(jobEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @HasPermission("job:update")
    @PostMapping("/update")
    @SaveLog(module = "任务管理", type = LogType.UPDATE)
    public String update(@RequestBody JobEntity jobEntity) {
        JobEntity result = jobService.updateEntity(jobEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @HasPermission("job:delete")
    @PostMapping("/delete")
    @SaveLog(module = "任务管理", type = LogType.DELETE)
    public String delete(@RequestBody JobEntity jobEntity) {
        return Result.status(jobService.delete(jobEntity));
    }

    /**
     * 执行一次
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @PostMapping("/trigger")
    @HasPermission("job:trigger")
    public String triggerJob(@RequestBody JobEntity jobEntity) {
        // force cover job param
        if (jobEntity.getExecutorParam() == null) {
            jobEntity.setExecutorParam("");
        }
        JobTriggerPoolHelper.trigger(jobEntity.getId(), TriggerTypeEnum.MANUAL, -1, null, jobEntity.getExecutorParam(), jobEntity.getAddressList());
        return Result.success();
    }

    /**
     * 启动
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @PostMapping("/start")
    @HasPermission("job:start")
    public String start(@RequestBody JobEntity jobEntity) {
        return Result.status(jobService.start(jobEntity));
    }

    /**
     * 停止
     *
     * @param jobEntity 任务bean
     * @return String
     */
    @PostMapping("/stop")
    @HasPermission("job:stop")
    public String stop(@RequestBody JobEntity jobEntity) {
        return Result.status(jobService.stop(jobEntity));
    }
}
