package com.smart.gen.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.gen.GenDemoEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.gen.GenDemoService;
import com.smart.tools.excel.ExcelExport;
import com.smart.tools.excel.ExcelImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 生成案例 Controller
 *
 * @author wf
 * @since 2024-08-19 00:28:40
 */
@RestController
@RequestMapping("/gen/genDemo")
public class GenDemoController {
    @Autowired
    GenDemoService genDemoService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param genDemoEntity 生成案例bean
     * @return String
     */
    @GetMapping("/page")
    public String page(GenDemoEntity genDemoEntity) {
        return Result.data(genDemoService.findPage(genDemoEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param genDemoEntity 生成案例bean
     * @return String
     */
    @GetMapping("/list")
    public String list(GenDemoEntity genDemoEntity) {
        return Result.data(genDemoService.findList(genDemoEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        GenDemoEntity result = genDemoService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param genDemoEntity 生成案例bean
     * @return String
     */
    @HasPermission("genDemo:add")
    @PostMapping("/save")
    @SaveLog(module = "生成案例管理" , type = LogType.ADD)
    public String save(@RequestBody GenDemoEntity genDemoEntity) {
        GenDemoEntity result = genDemoService.saveEntity(genDemoEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param genDemoEntity 生成案例bean
     * @return String
     */
    @HasPermission("genDemo:update")
    @PostMapping("/update")
    @SaveLog(module = "生成案例管理" , type = LogType.UPDATE)
    public String update(@RequestBody GenDemoEntity genDemoEntity) {
        GenDemoEntity result = genDemoService.updateEntity(genDemoEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param genDemoEntity 生成案例bean
     * @return String
     */
    @HasPermission("genDemo:delete")
    @PostMapping("/delete")
    @SaveLog(module = "生成案例管理" , type = LogType.DELETE)
    public String delete(@RequestBody GenDemoEntity genDemoEntity) {
        return Result.status(genDemoService.delete(genDemoEntity));
    }

    /**
     * 导出
     *
     * @param genDemoEntity 生成案例bean
     * @param response 响应
     */
    @HasPermission("genDemo:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody GenDemoEntity genDemoEntity, HttpServletResponse response) {
        List<GenDemoEntity> list = resultWrapper.setListIdentityInfo(genDemoService.findList(genDemoEntity), GenDemoEntity.class);
        String fileName = "生成案例数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("生成案例数据", GenDemoEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("genDemo:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<GenDemoEntity> dataList = excelImport.getDataList(GenDemoEntity.class);
            return Result.status(genDemoService.saveBatch(dataList));
        } catch (Exception e) {
            return Result.fail("导入失败！");
        }
    }

    /**
     * 下载导入模板
     *
     * @param response 响应
     */
    @GetMapping(value = "/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "导入生成案例模板.xlsx";
        try (ExcelExport export = new ExcelExport("生成案例数据", GenDemoEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }
}
