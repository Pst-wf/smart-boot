package com.smart.message.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.common.constant.SmartConstant;
import com.smart.entity.message.MessageEntity;
import com.smart.model.response.r.Result;
import com.smart.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 消息表 Controller
 *
 * @author wf
 * @since 2023-05-05 13:38:10
 */
@RestController
@RequestMapping("/message/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    /**
     * 列表
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @GetMapping("/page")
    public String page(MessageEntity messageEntity) {
        return Result.data(messageService.findPage(messageEntity));
    }

    /**
     * 回收站列表
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @GetMapping("/collectionPage")
    public String collectionPage(MessageEntity messageEntity) {
        return Result.data(messageService.collectionPage(messageEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        MessageEntity result = messageService.get(id);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 查看
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/view")
    public String view(@RequestParam String id) {
        MessageEntity result = messageService.view(id);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 保存
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/save")
    @SaveLog(module = "消息表管理", type = LogType.ADD)
    public String save(@RequestBody MessageEntity messageEntity) {
        messageEntity.setMessageStatus(SmartConstant.NO);
        MessageEntity result = messageService.saveEntity(messageEntity);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 修改
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/update")
    @SaveLog(module = "消息表管理", type = LogType.UPDATE)
    public String update(@RequestBody MessageEntity messageEntity) {
        messageEntity.setMessageStatus(SmartConstant.NO);
        MessageEntity result = messageService.updateEntity(messageEntity);
        return result != null ? Result.data(result) : Result.fail();
    }

    /**
     * 修改删除状态
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/updateDeletedValue")
    public String updateDeletedValue(@RequestBody MessageEntity messageEntity) {
        messageService.updateDeletedValue(messageEntity);
        return Result.success();
    }

    /**
     * 删除
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/delete")
    @SaveLog(module = "消息表管理", type = LogType.DELETE)
    public String delete(@RequestBody MessageEntity messageEntity) {
        return Result.status(messageService.delete(messageEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @GetMapping("/list")
    public String list(MessageEntity messageEntity) {
        return Result.data(messageService.findList(messageEntity));
    }

    /**
     * 发送信息
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/send")
    public String send(@RequestBody MessageEntity messageEntity) {
        messageService.send(messageEntity);
        return Result.success();
    }

    /**
     * 发送站内信
     *
     * @param messageEntity 消息表bean
     * @return String
     */
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody MessageEntity messageEntity) {
        messageService.sendMessage(messageEntity);
        return Result.success();
    }
}
