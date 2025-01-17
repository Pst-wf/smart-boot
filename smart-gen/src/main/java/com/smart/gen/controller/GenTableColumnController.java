package com.smart.gen.controller;


import com.smart.aop.permission.HasPermission;
import com.smart.entity.gen.GenTableColumnEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.gen.GenTableColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 代码生成表列 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/gen/genTableColumn")
public class GenTableColumnController {
    @Autowired
    GenTableColumnService genTableColumnService;

    /**
     * 列表
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @GetMapping("/page")
    public String page(GenTableColumnEntity genTableColumnEntity) {
        return Result.data(genTableColumnService.findPage(genTableColumnEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        GenTableColumnEntity result = genTableColumnService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @HasPermission("gen:add")
    @PostMapping("/save")
    public String save(@RequestBody GenTableColumnEntity genTableColumnEntity) {
        GenTableColumnEntity result = genTableColumnService.saveEntity(genTableColumnEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @HasPermission("gen:update")
    @PostMapping("/update")
    public String update(@RequestBody GenTableColumnEntity genTableColumnEntity) {
        GenTableColumnEntity result = genTableColumnService.updateEntity(genTableColumnEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @HasPermission("gen:delete")
    @PostMapping("/delete")
    public String delete(@RequestBody GenTableColumnEntity genTableColumnEntity) {
        return Result.status(genTableColumnService.delete(genTableColumnEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @GetMapping("/list")
    public String list(GenTableColumnEntity genTableColumnEntity) {
        return Result.data(genTableColumnService.findList(genTableColumnEntity));
    }

    /**
     * 通过表名获取数据库中的字段
     *
     * @param genTableColumnEntity 字段bean
     * @return String
     */
    @GetMapping("/findColumns")
    public String findColumns(GenTableColumnEntity genTableColumnEntity) {
        return Result.data(genTableColumnService.findColumns(genTableColumnEntity));
    }

}
