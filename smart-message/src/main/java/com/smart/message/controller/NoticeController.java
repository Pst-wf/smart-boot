package com.smart.message.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.message.NoticeEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.message.NoticeService;
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
 * 通知公告 Controller
 *
 * @author wf
 * @since 2024-12-13 17:10:35
 */
@RestController
@RequestMapping("/message/notice")
public class NoticeController {
    @Autowired
    NoticeService noticeService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param noticeEntity 通知公告bean
     * @return String
     */
    @GetMapping("/page")
    public String page(NoticeEntity noticeEntity) {
        return Result.data(noticeService.findPage(noticeEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param noticeEntity 通知公告bean
     * @return String
     */
    @GetMapping("/list")
    public String list(NoticeEntity noticeEntity) {
        return Result.data(noticeService.findList(noticeEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        NoticeEntity result = noticeService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param noticeEntity 通知公告bean
     * @return String
     */
    @HasPermission("notice:add")
    @PostMapping("/save")
    @SaveLog(module = "通知公告管理", type = LogType.ADD)
    public String save(@RequestBody NoticeEntity noticeEntity) {
        NoticeEntity result = noticeService.saveEntity(noticeEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param noticeEntity 通知公告bean
     * @return String
     */
    @HasPermission("notice:update")
    @PostMapping("/update")
    @SaveLog(module = "通知公告管理", type = LogType.UPDATE)
    public String update(@RequestBody NoticeEntity noticeEntity) {
        NoticeEntity result = noticeService.updateEntity(noticeEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param noticeEntity 通知公告bean
     * @return String
     */
    @HasPermission("notice:delete")
    @PostMapping("/delete")
    @SaveLog(module = "通知公告管理", type = LogType.DELETE)
    public String delete(@RequestBody NoticeEntity noticeEntity) {
        return Result.status(noticeService.delete(noticeEntity));
    }

    /**
     * 导出
     *
     * @param noticeEntity 通知公告bean
     * @param response     响应
     */
    @HasPermission("notice:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody NoticeEntity noticeEntity, HttpServletResponse response) {
        List<NoticeEntity> list = resultWrapper.setListIdentityInfo(noticeService.findList(noticeEntity), NoticeEntity.class);
        String fileName = "通知公告数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("通知公告数据", NoticeEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("notice:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<NoticeEntity> dataList = excelImport.getDataList(NoticeEntity.class);
            return Result.status(noticeService.saveBatch(dataList));
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
        String fileName = "导入通知公告模板.xlsx";
        try (ExcelExport export = new ExcelExport("通知公告数据", NoticeEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }
}
