package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.system.DeptEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.DeptService;
import com.smart.tools.excel.ExcelExport;
import com.smart.tools.excel.ExcelImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 机构 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/dept")
public class DeptController {
    @Autowired
    DeptService deptService;

    /**
     * 列表
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @GetMapping("/page")
    public String page(DeptEntity deptEntity) {
        return Result.data(deptService.findPage(deptEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        DeptEntity result = deptService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @HasPermission("dept:add")
    @PostMapping("/save")
    @SaveLog(module = "机构管理", type = LogType.ADD)
    public String save(@RequestBody DeptEntity deptEntity) {
        DeptEntity result = deptService.saveEntity(deptEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @HasPermission("dept:update")
    @PostMapping("/update")
    @SaveLog(module = "机构管理", type = LogType.UPDATE)
    public String update(@RequestBody DeptEntity deptEntity) {
        DeptEntity result = deptService.updateEntity(deptEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @HasPermission("dept:delete")
    @PostMapping("/delete")
    @SaveLog(module = "机构管理", type = LogType.DELETE)
    public String delete(@RequestBody DeptEntity deptEntity) {
        return Result.status(deptService.delete(deptEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @GetMapping("/list")
    public String list(DeptEntity deptEntity) {
        return Result.data(deptService.findList(deptEntity));
    }

    /**
     * 树形结构
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @GetMapping("/tree")
    public String tree(DeptEntity deptEntity) {
        return Result.data(deptService.getTree(deptEntity));
    }

    /**
     * 树形结构格式化
     *
     * @param deptEntity 机构bean
     * @return String
     */
    @GetMapping("/treeFormat")
    public String treeFormat(DeptEntity deptEntity) {
        List<Map<String, Object>> list = deptService.getTreeFormat(deptEntity);
        return Result.data(list);
    }

    /**
     * 导出
     */
    @HasPermission("dept:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody DeptEntity deptEntity, HttpServletResponse response) {
        List<DeptEntity> list = deptService.findExportList(deptEntity);
        String fileName = "部门机构数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("部门机构", DeptEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }


    /**
     * 下载导入模板
     *
     * @param response 响应
     */
    @GetMapping(value = "/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        String fileName = "导入组织机构模板.xlsx";
        try (ExcelExport export = new ExcelExport("组织机构数据", DeptEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("dept:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<DeptEntity> dataList = excelImport.getDataList(DeptEntity.class);
            dataList.forEach(x -> {
                x.setParentId("0");
                x.setAncestors("0");
                x.setSort(0);
                x.setStatus("1");
            });
            return Result.status(deptService.saveBatch(dataList));
        } catch (Exception e) {
            return Result.fail("导入失败！" + e.getMessage());
        }
    }

    /**
     * 修改状态
     *
     * @param entity bean实体
     * @return String
     */
    @HasPermission("dept:update")
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestBody DeptEntity entity) {
        return Result.status(deptService.updateStatus(entity));
    }

}
