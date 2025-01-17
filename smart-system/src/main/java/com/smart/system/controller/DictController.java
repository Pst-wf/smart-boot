package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.entity.system.DictEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 字典 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping({"/system/dict", "/front/system/dict"})
public class DictController {
    @Autowired
    DictService dictService;

    /**
     * 列表
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @GetMapping("/page")
    public String page(DictEntity dictEntity) {
        return Result.data(dictService.findPage(dictEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @GetMapping("/list")
    public String list(DictEntity dictEntity) {
        return Result.data(dictService.findList(dictEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        DictEntity result = dictService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @HasPermission({"dict:add", "dict:add:1", "dict:add:2"})
    @PostMapping("/save")
    @SaveLog(module = "字典管理", type = LogType.ADD)
    public String save(@RequestBody DictEntity dictEntity) {
        DictEntity result = dictService.saveEntity(dictEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @HasPermission({"dict:update", "dict:update:1", "dict:update:2"})
    @PostMapping("/update")
    @SaveLog(module = "字典管理", type = LogType.UPDATE)
    public String update(@RequestBody DictEntity dictEntity) {
        DictEntity result = dictService.updateEntity(dictEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @HasPermission({"dict:delete", "dict:delete:1", "dict:delete:2"})
    @PostMapping("/delete")
    @SaveLog(module = "字典管理", type = LogType.DELETE)
    public String delete(@RequestBody DictEntity dictEntity) {
        return Result.status(dictService.delete(dictEntity));
    }

    /**
     * 字典树
     *
     * @param dictEntity 字典bean
     * @return String
     */
    @GetMapping("/tree")
    public String tree(DictEntity dictEntity) {
        // 添加排序
        dictEntity.setSortField("dictType,sort");
        dictEntity.setSortOrder("ASC");
        return Result.data(dictService.getTree(dictEntity));
    }

    /**
     * 通过Codes获取字典
     *
     * @param codes 字典codes
     * @return String
     */
    @GetMapping("/getDictByCodes")
    public String getDictByCodes(@RequestParam String codes) {
        return Result.data(dictService.getDictByCodes(codes));
    }

    /**
     * 获取字典数据集
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/values")
    public String values(@RequestParam String id) {
        return Result.data(dictService.getValues(id));
    }

}
