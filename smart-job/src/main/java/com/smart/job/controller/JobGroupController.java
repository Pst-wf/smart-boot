package com.smart.job.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.job.JobGroupEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.job.JobGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 执行器 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/job/jobGroup")
public class JobGroupController {
    @Autowired
    JobGroupService jobGroupService;

    /**
     * 列表
     *
     * @param jobGroupEntity 执行器bean
     * @return String
     */
    @GetMapping("/page")
    public String page(JobGroupEntity jobGroupEntity) {
        return Result.data(jobGroupService.findPage(jobGroupEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        JobGroupEntity result = jobGroupService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param jobGroupEntity 执行器bean
     * @return String
     */
    @HasPermission("jobGroup:add")
    @PostMapping("/save")
    @SaveLog(module = "执行器管理", type = LogType.ADD)
    public String save(@RequestBody JobGroupEntity jobGroupEntity) {
        JobGroupEntity result = jobGroupService.saveEntity(jobGroupEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param jobGroupEntity 执行器bean
     * @return String
     */
    @HasPermission("jobGroup:update")
    @PostMapping("/update")
    @SaveLog(module = "执行器管理", type = LogType.UPDATE)
    public String update(@RequestBody JobGroupEntity jobGroupEntity) {
        JobGroupEntity result = jobGroupService.updateEntity(jobGroupEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param jobGroupEntity 执行器bean
     * @return String
     */
    @HasPermission("jobGroup:delete")
    @PostMapping("/delete")
    @SaveLog(module = "执行器管理", type = LogType.DELETE)
    public String delete(@RequestBody JobGroupEntity jobGroupEntity) {
        return Result.status(jobGroupService.delete(jobGroupEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param jobGroupEntity 执行器bean
     * @return String
     */
    @GetMapping("/list")
    public String list(JobGroupEntity jobGroupEntity) {
        return Result.data(jobGroupService.findList(jobGroupEntity));
    }
}
