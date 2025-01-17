package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.system.ErrorLogEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 错误日志 Controller
 *
 * @author wf
 * @since 2022-07-27 14:27:15
 */
@RestController
@RequestMapping("/system/errorLog")
public class ErrorLogController {
    @Autowired
    ErrorLogService errorLogService;

    /**
     * 列表
     *
     * @param errorLogEntity 错误日志bean
     * @return String
     */
    @GetMapping("/page")
    public String page(ErrorLogEntity errorLogEntity) {
        return Result.data(errorLogService.findPage(errorLogEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        ErrorLogEntity result = errorLogService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param errorLogEntity 错误日志bean
     * @return String
     */
    @HasPermission("errorLog:add")
    @PostMapping("/save")
    @SaveLog(module = "错误日志管理", type = LogType.ADD)
    public String save(@RequestBody ErrorLogEntity errorLogEntity) {
        ErrorLogEntity result = errorLogService.saveEntity(errorLogEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param errorLogEntity 错误日志bean
     * @return String
     */
    @HasPermission("errorLog:update")
    @PostMapping("/update")
    @SaveLog(module = "错误日志管理", type = LogType.UPDATE)
    public String update(@RequestBody ErrorLogEntity errorLogEntity) {
        ErrorLogEntity result = errorLogService.updateEntity(errorLogEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param errorLogEntity 错误日志bean
     * @return String
     */
    @HasPermission("errorLog:delete")
    @PostMapping("/delete")
    @SaveLog(module = "错误日志管理", type = LogType.DELETE)
    public String delete(@RequestBody ErrorLogEntity errorLogEntity) {
        return Result.status(errorLogService.delete(errorLogEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param errorLogEntity 错误日志bean
     * @return String
     */
    @GetMapping("/list")
    public String list(ErrorLogEntity errorLogEntity) {
        return Result.data(errorLogService.findList(errorLogEntity));
    }
}
