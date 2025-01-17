package com.smart.system.controller;

import com.github.pagehelper.Page;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.system.OssEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 对象存储 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/oss")
public class OssController {
    @Autowired
    OssService ossService;

    /**
     * 列表
     *
     * @param ossEntity 对象存储bean
     * @return String
     */
    @GetMapping("/page")
    public String page(OssEntity ossEntity) {
        return Result.data(ossService.findPage(ossEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        OssEntity result = ossService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param ossEntity 对象存储bean
     * @return String
     */
    @HasPermission("oss:add")
    @PostMapping("/save")
    @SaveLog(module = "对象存储管理", type = LogType.ADD)
    public String save(@RequestBody OssEntity ossEntity) {
        OssEntity result = ossService.saveEntity(ossEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param ossEntity 对象存储bean
     * @return String
     */
    @HasPermission("oss:update")
    @PostMapping("/update")
    @SaveLog(module = "对象存储管理", type = LogType.UPDATE)
    public String update(@RequestBody OssEntity ossEntity) {
        OssEntity result = ossService.updateEntity(ossEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param ossEntity 对象存储bean
     * @return String
     */
    @HasPermission("oss:delete")
    @PostMapping("/delete")
    @SaveLog(module = "对象存储管理", type = LogType.DELETE)
    public String delete(@RequestBody OssEntity ossEntity) {
        return Result.status(ossService.delete(ossEntity));
    }

    /**
     * 修改启动状态
     *
     * @param ossEntity 对象存储bean
     * @return String
     */
    @HasPermission("oss:update")
    @PostMapping("/updateOssStatus")
    public String updateOssStatus(@RequestBody OssEntity ossEntity) {
        return Result.status(ossService.updateOssStatus(ossEntity));
    }
}
