package com.smart.job.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.job.JobLogEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.job.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 任务执行日志 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/job/jobLog")
public class JobLogController {
    @Autowired
    JobLogService jobLogService;

    /**
     * 列表
     *
     * @param jobLogEntity 任务执行日志bean
     * @return String
     */
    @GetMapping("/page")
    public String page(JobLogEntity jobLogEntity) {
        jobLogEntity.setSortField(JobLogEntity.Fields.triggerTime);
        jobLogEntity.setSortOrder("DESC");
        return Result.data(jobLogService.findPage(jobLogEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        JobLogEntity result = jobLogService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param jobLogEntity 任务执行日志bean
     * @return String
     */
    @HasPermission("jobLog:add")
    @PostMapping("/save")
    @SaveLog(module = "任务执行日志管理", type = LogType.ADD)
    public String save(@RequestBody JobLogEntity jobLogEntity) {
        JobLogEntity result = jobLogService.saveEntity(jobLogEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param jobLogEntity 任务执行日志bean
     * @return String
     */
    @HasPermission("jobLog:update")
    @PostMapping("/update")
    @SaveLog(module = "任务执行日志管理", type = LogType.UPDATE)
    public String update(@RequestBody JobLogEntity jobLogEntity) {
        JobLogEntity result = jobLogService.updateEntity(jobLogEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param jobLogEntity 任务执行日志bean
     * @return String
     */
    @HasPermission("jobLog:delete")
    @PostMapping("/delete")
    @SaveLog(module = "任务执行日志管理", type = LogType.DELETE)
    public String delete(@RequestBody JobLogEntity jobLogEntity) {
        return Result.status(jobLogService.delete(jobLogEntity));
    }

}
