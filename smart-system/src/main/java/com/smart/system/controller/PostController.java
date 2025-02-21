package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.system.PostEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 岗位 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/post")
public class PostController {
    @Autowired
    PostService postService;

    /**
     * 列表
     *
     * @param postEntity 岗位bean
     * @return String
     */
    @GetMapping("/page")
    public String page(PostEntity postEntity) {
        return Result.data(postService.findPage(postEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param postEntity 岗位bean
     * @return String
     */
    @GetMapping("/list")
    public String list(PostEntity postEntity) {
        return Result.data(postService.findList(postEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        PostEntity result = postService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param postEntity 岗位bean
     * @return String
     */
    @HasPermission("post:add")
    @PostMapping("/save")
    @SaveLog(module = "岗位管理", type = LogType.ADD)
    public String save(@RequestBody PostEntity postEntity) {
        PostEntity result = postService.saveEntity(postEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param postEntity 岗位bean
     * @return String
     */
    @HasPermission("post:update")
    @PostMapping("/update")
    @SaveLog(module = "岗位管理", type = LogType.UPDATE)
    public String update(@RequestBody PostEntity postEntity) {
        PostEntity result = postService.updateEntity(postEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param postEntity 岗位bean
     * @return String
     */
    @HasPermission("post:delete")
    @PostMapping("/delete")
    @SaveLog(module = "岗位管理", type = LogType.DELETE)
    public String delete(@RequestBody PostEntity postEntity) {
        return Result.status(postService.delete(postEntity));
    }

    /**
     * 修改状态
     *
     * @param entity bean实体
     * @return String
     */
    @HasPermission("post:update")
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestBody PostEntity entity) {
        return Result.status(postService.updateStatus(entity));
    }
}
