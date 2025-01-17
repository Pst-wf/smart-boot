package com.smart.message.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.entity.message.MessageUserEntity;
import com.smart.model.response.r.Result;
import com.smart.service.message.MessageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 消息-用户表 Controller
 *
 * @author wf
 * @since 2023-05-05 13:39:59
 */
@RestController
@RequestMapping("/message/messageUser")
public class MessageUserController {
    @Autowired
    MessageUserService messageUserService;

    /**
     * 列表
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @GetMapping("/page")
    public String page(MessageUserEntity messageUserEntity) {
        return Result.data(messageUserService.findPage(messageUserEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        MessageUserEntity result = messageUserService.get(id);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 保存
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @PostMapping("/save")
    @SaveLog(module = "消息-用户表管理", type = LogType.ADD)
    public String save(@RequestBody MessageUserEntity messageUserEntity) {
        MessageUserEntity result = messageUserService.saveEntity(messageUserEntity);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 修改
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @PostMapping("/update")
    @SaveLog(module = "消息-用户表管理", type = LogType.UPDATE)
    public String update(@RequestBody MessageUserEntity messageUserEntity) {
        MessageUserEntity result = messageUserService.updateEntity(messageUserEntity);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 删除
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @PostMapping("/updateMessageDeletedStatus")
    public String updateMessageDeletedStatus(@RequestBody MessageUserEntity messageUserEntity) {
        return Result.status(messageUserService.updateMessageDeletedStatus(messageUserEntity));
    }

    /**
     * 彻底删除
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @PostMapping("/delete")
    @SaveLog(module = "消息-用户表管理", type = LogType.DELETE)
    public String delete(@RequestBody MessageUserEntity messageUserEntity) {
        return Result.status(messageUserService.delete(messageUserEntity));
    }

    /**
     * 标记已读
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @PostMapping("/setRead")
    public String setRead(@RequestBody MessageUserEntity messageUserEntity) {
        return Result.status(messageUserService.setRead(messageUserEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param messageUserEntity 消息-用户表bean
     * @return String
     */
    @GetMapping("/list")
    public String list(MessageUserEntity messageUserEntity) {
        return Result.data(messageUserService.findList(messageUserEntity));
    }


    /**
     * 列表
     *
     * @return String
     */
    @GetMapping("/messageCount")
    public String messageCount() {
        return Result.data(messageUserService.getMessageCount());
    }
}
