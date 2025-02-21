package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.system.RegionEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.system.RegionService;
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
 * 行政区域 Controller
 *
 * @author wf
 * @since 2025-02-20 16:59:05
 */
@RestController
@RequestMapping("/system/region")
public class RegionController {
    @Autowired
    RegionService regionService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @GetMapping("/page")
    public String page(RegionEntity regionEntity) {
        return Result.data(regionService.findPage(regionEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @GetMapping("/list")
    public String list(RegionEntity regionEntity) {
        return Result.data(regionService.findList(regionEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        RegionEntity result = regionService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @HasPermission("region:add")
    @PostMapping("/save")
    @SaveLog(module = "行政区域管理", type = LogType.ADD)
    public String save(@RequestBody RegionEntity regionEntity) {
        RegionEntity result = regionService.saveEntity(regionEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @HasPermission("region:update")
    @PostMapping("/update")
    @SaveLog(module = "行政区域管理", type = LogType.UPDATE)
    public String update(@RequestBody RegionEntity regionEntity) {
        RegionEntity result = regionService.updateEntity(regionEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @HasPermission("region:delete")
    @PostMapping("/delete")
    @SaveLog(module = "行政区域管理", type = LogType.DELETE)
    public String delete(@RequestBody RegionEntity regionEntity) {
        return Result.status(regionService.delete(regionEntity));
    }

    /**
     * 导出
     *
     * @param regionEntity 行政区域bean
     * @param response     响应
     */
    @HasPermission("region:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody RegionEntity regionEntity, HttpServletResponse response) {
        List<RegionEntity> list = resultWrapper.setListIdentityInfo(regionService.findList(regionEntity), RegionEntity.class);
        String fileName = "行政区域数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("行政区域数据", RegionEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("region:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<RegionEntity> dataList = excelImport.getDataList(RegionEntity.class);
            return Result.status(regionService.saveBatch(dataList));
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
        String fileName = "导入行政区域模板.xlsx";
        try (ExcelExport export = new ExcelExport("行政区域数据", RegionEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }

    /**
     * 树形结构
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @GetMapping("/asyncLoading")
    public String asyncLoading(RegionEntity regionEntity) {
        return Result.data(regionService.asyncLoading(regionEntity));
    }

    /**
     * 树形结构
     *
     * @param regionEntity 行政区域bean
     * @return String
     */
    @GetMapping("/tree")
    public String tree(RegionEntity regionEntity) {
        return Result.data(regionService.tree(regionEntity));
    }

    /**
     * 树形结构格式化
     *
     * @param regionEntity 机构bean
     * @return String
     */
    @GetMapping("/treeFormat")
    public String treeFormat(RegionEntity regionEntity) {
        List<Map<String, Object>> list = regionService.getTreeFormat(regionEntity);
        return Result.data(list);
    }

    /**
     * 修改状态
     *
     * @param entity bean实体
     * @return String
     */
    @HasPermission("region:update")
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestBody RegionEntity entity) {
        return Result.status(regionService.updateStatus(entity));
    }
}
