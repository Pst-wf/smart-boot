package com.smart.system.controller;

import com.smart.aop.permission.HasPermission;
import com.smart.model.redis.RedisModel;
import com.smart.model.response.r.Result;
import com.smart.system.service.RedisOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 缓存 Controller
 *
 * @author wf
 * @since 2023-05-30 15:29:15
 */
@RestController
@RequestMapping("/system/redis")
public class RedisController {
    @Autowired
    RedisOptService redisOptService;

    /**
     * 列表
     *
     * @return String
     */
    @GetMapping("/query")
    public String query(@RequestParam(value = "key", required = false) String key, @RequestParam(value = "showType", required = false) String showType, @RequestParam(value = "type", required = false) String type) {
        return Result.data(redisOptService.query(key, showType, type));
    }


    /**
     * 详情
     *
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam(value = "key", required = false) String key) {
        return Result.data(redisOptService.getOne(key));
    }

    /**
     * 删除缓存数据
     *
     * @return String
     */
    @HasPermission("redis:delete")
    @PostMapping("/delete")
    public String delete(@RequestBody RedisModel redisModel) {
        redisOptService.delete(redisModel);
        return Result.success();
    }


    /**
     * 新增修改缓存数据
     *
     * @return String
     */
    @HasPermission({"redis:add", "redis:update"})
    @PostMapping("/saveOrUpdate")
    public String saveOrUpdate(@RequestBody RedisModel redisModel) {
        redisOptService.saveOrUpdate(redisModel);
        return Result.success();
    }
}
