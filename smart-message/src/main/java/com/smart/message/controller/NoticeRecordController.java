package com.smart.message.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.message.NoticeRecordEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.message.NoticeRecordService;
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
 * 通知公告发布记录 Controller
 *
 * @author wf
 * @since 2024-12-13 17:46:05
 */
@RestController
@RequestMapping("/message/noticeRecord")
public class NoticeRecordController {
    @Autowired
    NoticeRecordService noticeRecordService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @return String
     */
    @GetMapping("/page")
    public String page(NoticeRecordEntity noticeRecordEntity) {
        return Result.data(noticeRecordService.findPage(noticeRecordEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @return String
     */
    @GetMapping("/list")
    public String list(NoticeRecordEntity noticeRecordEntity) {
        return Result.data(noticeRecordService.findList(noticeRecordEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        NoticeRecordEntity result = noticeRecordService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @return String
     */
    @HasPermission("noticeRecord:add")
    @PostMapping("/save")
    @SaveLog(module = "通知公告发布记录管理" , type = LogType.ADD)
    public String save(@RequestBody NoticeRecordEntity noticeRecordEntity) {
        NoticeRecordEntity result = noticeRecordService.saveEntity(noticeRecordEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @return String
     */
    @HasPermission("noticeRecord:update")
    @PostMapping("/update")
    @SaveLog(module = "通知公告发布记录管理" , type = LogType.UPDATE)
    public String update(@RequestBody NoticeRecordEntity noticeRecordEntity) {
        NoticeRecordEntity result = noticeRecordService.updateEntity(noticeRecordEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @return String
     */
    @HasPermission("noticeRecord:delete")
    @PostMapping("/delete")
    @SaveLog(module = "通知公告发布记录管理" , type = LogType.DELETE)
    public String delete(@RequestBody NoticeRecordEntity noticeRecordEntity) {
        return Result.status(noticeRecordService.delete(noticeRecordEntity));
    }

    /**
     * 导出
     *
     * @param noticeRecordEntity 通知公告发布记录bean
     * @param response 响应
     */
    @HasPermission("noticeRecord:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody NoticeRecordEntity noticeRecordEntity, HttpServletResponse response) {
        List<NoticeRecordEntity> list = resultWrapper.setListIdentityInfo(noticeRecordService.findList(noticeRecordEntity), NoticeRecordEntity.class);
        String fileName = "通知公告发布记录数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("通知公告发布记录数据", NoticeRecordEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("noticeRecord:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<NoticeRecordEntity> dataList = excelImport.getDataList(NoticeRecordEntity.class);
            return Result.status(noticeRecordService.saveBatch(dataList));
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
        String fileName = "导入通知公告发布记录模板.xlsx";
        try (ExcelExport export = new ExcelExport("通知公告发布记录数据", NoticeRecordEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }
}
