package com.smart.system.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.system.FrontUserEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.system.FrontUserService;
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
 * 前台用户 Controller
 *
 * @author wf
 * @since 2024-08-08 16:20:30
 */
@RestController
@RequestMapping("/system/frontUser")
public class FrontUserController {
    @Autowired
    FrontUserService frontUserService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param frontUserEntity 前台用户bean
     * @return String
     */
    @GetMapping("/page")
    public String page(FrontUserEntity frontUserEntity) {
        return Result.data(frontUserService.findPage(frontUserEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param frontUserEntity 前台用户bean
     * @return String
     */
    @GetMapping("/list")
    public String list(FrontUserEntity frontUserEntity) {
        return Result.data(frontUserService.findList(frontUserEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        FrontUserEntity result = frontUserService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param frontUserEntity 前台用户bean
     * @return String
     */
    @HasPermission("frontUser:add")
    @PostMapping("/save")
    @SaveLog(module = "前台用户管理", type = LogType.ADD)
    public String save(@RequestBody FrontUserEntity frontUserEntity) {
        FrontUserEntity result = frontUserService.saveEntity(frontUserEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param frontUserEntity 前台用户bean
     * @return String
     */
    @HasPermission("frontUser:update")
    @PostMapping("/update")
    @SaveLog(module = "前台用户管理", type = LogType.UPDATE)
    public String update(@RequestBody FrontUserEntity frontUserEntity) {
        FrontUserEntity result = frontUserService.updateEntity(frontUserEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param frontUserEntity 前台用户bean
     * @return String
     */
    @HasPermission("frontUser:delete")
    @PostMapping("/delete")
    @SaveLog(module = "前台用户管理", type = LogType.DELETE)
    public String delete(@RequestBody FrontUserEntity frontUserEntity) {
        return Result.status(frontUserService.delete(frontUserEntity));
    }

    /**
     * 导出
     *
     * @param frontUserEntity 前台用户bean
     * @param response        响应
     */
    @HasPermission("frontUser:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody FrontUserEntity frontUserEntity, HttpServletResponse response) {
        List<FrontUserEntity> list = resultWrapper.setListIdentityInfo(frontUserService.findList(frontUserEntity), FrontUserEntity.class);
        String fileName = "前台用户数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("前台用户数据", FrontUserEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("frontUser:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<FrontUserEntity> dataList = excelImport.getDataList(FrontUserEntity.class);
            return Result.status(frontUserService.saveBatch(dataList));
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
        String fileName = "导入前台用户模板.xlsx";
        try (ExcelExport export = new ExcelExport("前台用户数据", FrontUserEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }

    /**
     * 修改用户状态
     *
     * @param frontUserEntity 用户bean
     * @return String
     */
    @HasPermission("frontUser:update")
    @PostMapping("/updateUserStatus")
    public String updateUserStatus(@RequestBody FrontUserEntity frontUserEntity) {
        return Result.status(frontUserService.updateUserStatus(frontUserEntity));
    }
}
