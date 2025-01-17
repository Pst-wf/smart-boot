package com.smart.system.controller;

import com.github.pagehelper.Page;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.AuthUtil;
import com.smart.entity.system.LoginLogEntity;
import com.smart.model.response.r.Result;
import com.smart.service.system.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 登录日志 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/loginLog")
public class LoginLogController {
    @Autowired
    LoginLogService loginLogService;

    /**
     * 列表
     *
     * @param loginLogEntity 日志bean
     * @return String
     */
    @GetMapping("/page")
    public String page(LoginLogEntity loginLogEntity) {
        Page<LoginLogEntity> page = loginLogService.findPage(loginLogEntity);
        return Result.data(page);
    }

    /**
     * 删除
     *
     * @param loginLogEntity 日志bean
     * @return String
     */
    @HasPermission("loginLog:delete")
    @PostMapping("/delete")
    @SaveLog(module = "登录日志", type = LogType.DELETE)
    public String delete(@RequestBody LoginLogEntity loginLogEntity) {
        return Result.status(loginLogService.delete(loginLogEntity));
    }

    /**
     * 列表
     *
     * @param loginLogEntity 日志bean
     * @return String
     */
    @GetMapping("/userLogs")
    public String userLogs(LoginLogEntity loginLogEntity) {
        loginLogEntity.setUserId(AuthUtil.getUserId());
        Page<LoginLogEntity> page = loginLogService.findPage(loginLogEntity);
        return Result.data(page);
    }
}
