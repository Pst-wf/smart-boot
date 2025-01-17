package com.smart.message.controller;

import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.DateUtil;
import com.smart.entity.message.NoticeRefEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.model.response.r.ResultWrapper;
import com.smart.service.message.NoticeRefService;
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
 * 通知公告-用户 Controller
 *
 * @author wf
 * @since 2024-12-13 17:48:13
 */
@RestController
@RequestMapping("/message/noticeRef")
public class NoticeRefController {
    @Autowired
    NoticeRefService noticeRefService;
    @Autowired
    ResultWrapper resultWrapper;

    /**
     * 列表
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @GetMapping("/page")
    public String page(NoticeRefEntity noticeRefEntity) {
        return Result.data(noticeRefService.findPage(noticeRefEntity));
    }

    /**
     * 列表（不分页）
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @GetMapping("/list")
    public String list(NoticeRefEntity noticeRefEntity) {
        return Result.data(noticeRefService.findList(noticeRefEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        NoticeRefEntity result = noticeRefService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @HasPermission("noticeRef:add")
    @PostMapping("/save")
    @SaveLog(module = "通知公告-用户管理", type = LogType.ADD)
    public String save(@RequestBody NoticeRefEntity noticeRefEntity) {
        NoticeRefEntity result = noticeRefService.saveEntity(noticeRefEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @HasPermission("noticeRef:update")
    @PostMapping("/update")
    @SaveLog(module = "通知公告-用户管理", type = LogType.UPDATE)
    public String update(@RequestBody NoticeRefEntity noticeRefEntity) {
        NoticeRefEntity result = noticeRefService.updateEntity(noticeRefEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @HasPermission("noticeRef:delete")
    @PostMapping("/delete")
    @SaveLog(module = "通知公告-用户管理", type = LogType.DELETE)
    public String delete(@RequestBody NoticeRefEntity noticeRefEntity) {
        return Result.status(noticeRefService.delete(noticeRefEntity));
    }

    /**
     * 导出
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @param response        响应
     */
    @HasPermission("noticeRef:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody NoticeRefEntity noticeRefEntity, HttpServletResponse response) {
        List<NoticeRefEntity> list = resultWrapper.setListIdentityInfo(noticeRefService.findList(noticeRefEntity), NoticeRefEntity.class);
        String fileName = "通知公告-用户数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("通知公告-用户数据", NoticeRefEntity.class)) {
            export.setDataList(list).write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("noticeRef:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<NoticeRefEntity> dataList = excelImport.getDataList(NoticeRefEntity.class);
            return Result.status(noticeRefService.saveBatch(dataList));
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
        String fileName = "导入通知公告-用户模板.xlsx";
        try (ExcelExport export = new ExcelExport("通知公告-用户数据", NoticeRefEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }

    /**
     * 标记已读
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @PostMapping("/read")
    public String read(@RequestBody NoticeRefEntity noticeRefEntity) {
        return Result.status(noticeRefService.read(noticeRefEntity.getId()));
    }

    /**
     * 修改
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @HasPermission("noticeRef:update")
    @PostMapping("/cancel")
    public String cancel(@RequestBody NoticeRefEntity noticeRefEntity) {
        return Result.status(noticeRefService.cancel(noticeRefEntity.getId()));
    }


    /**
     * 查询用户通知公告
     *
     * @param noticeRefEntity 通知公告bean
     * @return String
     */
    @GetMapping("/pageForUser")
    public String pageForUser(NoticeRefEntity noticeRefEntity) {
        return Result.data(noticeRefService.pageForUser(noticeRefEntity));
    }

    /**
     * 用户删除
     *
     * @param noticeRefEntity 通知公告-用户bean
     * @return String
     */
    @PostMapping("/deleteByUser")
    @SaveLog(module = "通知公告-用户管理", type = LogType.DELETE)
    public String deleteByUser(@RequestBody NoticeRefEntity noticeRefEntity) {
        return Result.status(noticeRefService.delete(noticeRefEntity));
    }
}
