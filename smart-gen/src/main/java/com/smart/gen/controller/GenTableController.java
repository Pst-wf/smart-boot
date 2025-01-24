package com.smart.gen.controller;


import com.smart.aop.permission.HasPermission;
import com.smart.entity.gen.GenTableEntity;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.gen.GenTableService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * 代码生成表 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/gen/genTable")
public class GenTableController {
    @Autowired
    GenTableService genTableService;

    /**
     * 列表
     *
     * @param genTableEntity 表bean
     * @return String
     */
    @GetMapping("/page")
    public String page(GenTableEntity genTableEntity) {
        return Result.data(genTableService.findPage(genTableEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        GenTableEntity result = genTableService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param genTableEntity 表bean
     * @return String
     */
    @HasPermission("gen:add")
    @PostMapping("/save")
    public String save(@RequestBody GenTableEntity genTableEntity) {
        GenTableEntity result = genTableService.saveEntity(genTableEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param genTableEntity 表bean
     * @return String
     */
    @HasPermission("gen:update")
    @PostMapping("/update")
    public String update(@RequestBody GenTableEntity genTableEntity) {
        GenTableEntity result = genTableService.updateEntity(genTableEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param genTableEntity 表bean
     * @return String
     */
    @HasPermission("gen:delete")
    @PostMapping("/delete")
    public String delete(@RequestBody GenTableEntity genTableEntity) {
        return Result.status(genTableService.delete(genTableEntity));
    }

    /**
     * 获取所有表信息
     *
     * @return String
     */
    @GetMapping("/findTables")
    public String findTableList() {
        List<GenTableEntity> list = genTableService.findTables();
        return Result.data(list);
    }

    /**
     * 生成代码
     *
     * @param genTableEntity 要生成的表bean
     * @param response       响应
     */
    @PostMapping("/code")
    public void code(@RequestBody GenTableEntity genTableEntity, HttpServletResponse response) throws IOException {
        byte[] data = genTableService.generatorCode(genTableEntity);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"smart_code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 生成代码到指定目录
     *
     * @param genTableEntity 要生成的表bean
     */
    @PostMapping("/generatorCodeInFile")
    public String generatorCodeInFile(@RequestBody GenTableEntity genTableEntity) {
        genTableService.generatorCodeInFile(genTableEntity.getId());
        return Result.success();
    }

    /**
     * 获取所有表信息
     *
     * @return String
     */
    @GetMapping("/previewCode")
    public String previewCode(@RequestParam("id") String id, @RequestParam("frontType") String frontType) {
        return Result.data(genTableService.previewCode(id, frontType));
    }

    /**
     * 获取JAVA工程根目录
     *
     * @return String
     */
    @GetMapping("/getWorkSpace")
    public String getWorkSpace() {
        return Result.data(genTableService.getWorkSpace());
    }
}
