package com.smart.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.EncodeUtil;
import com.smart.common.utils.ListUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.file.FileEntity;
import com.smart.model.exception.SmartException;
import com.smart.model.file.DocumentCallBackVO;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.smart.common.constant.SmartConstant.NO;


/**
 * 文件中心 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Slf4j
@RestController
@RequestMapping({"/file", "/front/file"})
public class FileController {
    @Autowired
    FileService fileService;

    /**
     * 列表
     *
     * @param fileEntity 文件中心bean
     * @return String
     */
    @GetMapping("/page")
    public String page(FileEntity fileEntity) {
        return Result.data(fileService.findPage(fileEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        FileEntity result = fileService.get(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param fileEntity 文件中心bean
     * @return String
     */
    @HasPermission("file:add")
    @PostMapping("/save")
    @SaveLog(module = "文件中心管理", type = LogType.ADD)
    public String save(@RequestBody FileEntity fileEntity) {
        FileEntity result = fileService.saveEntity(fileEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param fileEntity 文件中心bean
     * @return String
     */
    @HasPermission("file:update")
    @PostMapping("/update")
    @SaveLog(module = "文件中心管理", type = LogType.UPDATE)
    public String update(@RequestBody FileEntity fileEntity) {
        FileEntity result = fileService.updateEntity(fileEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param fileEntity 文件中心bean
     * @return String
     */
    @HasPermission("file:delete")
    @PostMapping("/delete")
    @SaveLog(module = "文件中心管理", type = LogType.DELETE)
    public String delete(@RequestBody FileEntity fileEntity) {
        return Result.status(fileService.delete(fileEntity));
    }

    /**
     * 上传
     */
    @PostMapping("/upload")
    public String upload(MultipartHttpServletRequest request) throws Exception {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        List<FileEntity> result = fileService.upload(files, true);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 上传
     */
    @PostMapping("/uploadApp")
    public String upload(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<FileEntity> result = fileService.upload(Arrays.asList(files), true);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 下载
     */
    @PostMapping(value = "/download")
    public void download(@RequestBody FileEntity fileEntity, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = fileService.download(fileEntity);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            response.addHeader("Content-Disposition", "attachment; filename=" + EncodeUtil.encodeUrl(fileEntity.getFileName()));
            response.setContentType("application/octet-stream");
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭输入流
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载（支持批量下载）
     */

    @GetMapping("/download")
    public void download(@RequestParam("ids") String ids, HttpServletResponse response) {
        if (StringUtils.isBlank(ids)) {
            throw new SmartException("文件编码为空！");
        }
        List<String> idsList = Arrays.asList(ids.split(","));
        List<FileEntity> list = fileService.list(new LambdaQueryWrapper<FileEntity>().in(FileEntity::getId, idsList).eq(FileEntity::getIsDeleted, NO));
        if (list.isEmpty()) {
            throw new SmartException("下载的文件不存在！");
        }

        if (list.size() > 1) {
            log.info("文件数大于1，批量下载文件");
            fileService.downloadBatch(list, response);
        } else {
            log.info("文件数量等于1，下载单个文件");
            fileService.downloadSingle(list.get(0), response);
        }
    }

    /**
     * 文件夹目录
     */
    @PostMapping(value = "/fileFolder")
    public String fileFolder() {

        return Result.success();
    }

    /**
     * 格式转换
     */
    @PostMapping("/format")
    public void format(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> fileList = multiFileMap.get("file");
        String type = request.getParameter("type");
        if (ListUtil.isEmpty(fileList)) {
            throw new SmartException("文件获取失败！");
        }
        fileService.format(fileList.get(0), type, response);
    }

    /**
     * 在线编辑
     */
    @GetMapping("/online/{id}")
    public void online(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        fileService.online(request, response, id);
    }

    /**
     * 在线编辑
     */
    @GetMapping("/onlineModal/{id}")
    public String onlineModal(@PathVariable String id) {
        return Result.data(fileService.onlineModal(id));
    }

    /**
     * 文件编辑回调
     *
     * @param documentCallBackVO 回调模型 (在线编辑)
     * @param id                 文件基础表唯一标识
     */
    @PostMapping("callback")
    @ResponseBody
    public Map<String, Object> callback(@RequestBody DocumentCallBackVO documentCallBackVO, String id) throws Exception {
        fileService.callback(documentCallBackVO, id);
        Map<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("error", 0);
        return objectObjectHashMap;
    }

    /**
     * 根据IDS获取文件
     *
     * @param ids 文件IDS
     * @return String
     */
    @GetMapping("/findListByIds")
    public String findListByIds(String ids) {
        return Result.data(fileService.getListByIds(ids));
    }
}
