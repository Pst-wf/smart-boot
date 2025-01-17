package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.AuthUtil;
import com.smart.entity.system.LogEntity;
import com.smart.model.response.r.Result;
import com.smart.service.system.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 日志 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/log")
public class LogController {
    @Autowired
    LogService logService;

    /**
     * 列表
     *
     * @param logEntity 日志bean
     * @return String
     */
    @GetMapping("/page")
    public String page(LogEntity logEntity) {
        return Result.data(logService.findPage(logEntity));
    }

    /**
     * 删除
     *
     * @param logEntity 日志bean
     * @return String
     */
    @HasPermission("log:delete")
    @PostMapping("/delete")
    @SaveLog(module = "操作日志", type = LogType.DELETE)
    public String delete(@RequestBody LogEntity logEntity) {
        return Result.status(logService.delete(logEntity));
    }

    /**
     * 列表
     *
     * @param logEntity 日志bean
     * @return String
     */
    @GetMapping("/userLogs")
    public String userLogs(LogEntity logEntity) {
        logEntity.setUserId(AuthUtil.getUserId());
        return Result.data(logService.findPage(logEntity));
    }
}
