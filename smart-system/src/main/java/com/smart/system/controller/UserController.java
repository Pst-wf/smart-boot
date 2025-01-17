package com.smart.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.smart.aop.log.LogType;
import com.smart.aop.log.SaveLog;
import com.smart.aop.permission.HasPermission;
import com.smart.common.utils.AuthUtil;
import com.smart.common.utils.CryptoUtil;
import com.smart.common.utils.DateUtil;
import com.smart.common.utils.DigestUtil;
import com.smart.entity.system.UserEntity;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.response.r.Result;
import com.smart.model.response.r.ResultCode;
import com.smart.service.system.UserService;
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
 * 用户 Controller
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@RestController
@RequestMapping("/system/user")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 列表
     *
     * @param userEntity 用户bean
     * @return String
     */
    @GetMapping("/page")
    public String page(UserEntity userEntity) {
        return Result.data(userService.findPage(userEntity));
    }

    /**
     * 集合
     *
     * @param userEntity 用户bean
     * @return String
     */
    @GetMapping("/list")
    public String list(UserEntity userEntity) {
        return Result.data(userService.findList(userEntity));
    }

    /**
     * 信息
     *
     * @param id 主键ID
     * @return String
     */
    @GetMapping("/get")
    public String get(@RequestParam String id) {
        UserEntity result = userService.getUserWithIdentity(id);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 保存
     *
     * @param userEntity 用户bean
     * @return String
     */
    @HasPermission("user:add")
    @PostMapping("/save")
    @SaveLog(module = "用户管理", type = LogType.ADD)
    public String save(@RequestBody UserEntity userEntity) {
        UserEntity result = userService.saveEntity(userEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改
     *
     * @param userEntity 用户bean
     * @return String
     */
    @HasPermission("user:update")
    @PostMapping("/update")
    @SaveLog(module = "用户管理", type = LogType.UPDATE)
    public String update(@RequestBody UserEntity userEntity) {
        UserEntity result = userService.updateEntity(userEntity);
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 删除
     *
     * @param userEntity 用户bean
     * @return String
     */
    @HasPermission("user:delete")
    @PostMapping("/delete")
    @SaveLog(module = "用户管理", type = LogType.DELETE)
    public String delete(@RequestBody UserEntity userEntity) {
        return Result.status(userService.delete(userEntity));
    }

    /**
     * 修改用户状态
     *
     * @param userEntity 用户bean
     * @return String
     */
    @HasPermission("user:update")
    @PostMapping("/updateUserStatus")
    public String updateUserStatus(@RequestBody UserEntity userEntity) {
        return Result.status(userService.updateUserStatus(userEntity));
    }

    /**
     * 导出
     *
     * @param userEntity 用户bean
     * @param response   响应
     */
    @HasPermission("user:export")
    @PostMapping(value = "/export")
    public void exportData(@RequestBody UserEntity userEntity, HttpServletResponse response) {
        List<UserEntity> list = userService.findList(userEntity);
        String fileName = "用户数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
        try (ExcelExport export = new ExcelExport("用户数据", UserEntity.class)) {
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
        String fileName = "导入用户模板.xlsx";
        try (ExcelExport export = new ExcelExport("用户数据", UserEntity.class, null, ExcelField.Type.IMPORT)) {
            export.write(response, fileName);
        }
    }

    /**
     * 导入
     *
     * @param request 请求
     */
    @HasPermission("user:import")
    @PostMapping(value = "/import")
    public String importData(MultipartHttpServletRequest request) {
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        List<MultipartFile> files = multiFileMap.get("files");
        try (ExcelImport excelImport = new ExcelImport(files.get(0), 2, 0)) {
            List<UserEntity> dataList = excelImport.getDataList(UserEntity.class);
            dataList.forEach(x -> {
                // 导入默认密码
                x.setPassword(DigestUtil.md5Hex("123456"));
                x.setPasswordBase(CryptoUtil.encrypt("123456"));
            });
            return Result.status(userService.saveBatch(dataList));
        } catch (Exception e) {
            return Result.fail("导入失败！" + e.getMessage());
        }
    }

    /**
     * 修改个人信息
     *
     * @param userEntity 用户bean
     * @return String
     */
    @PostMapping("/updateInfo")
    @SaveLog(module = "个人信息", type = LogType.UPDATE)
    public String updateInfo(@RequestBody UserEntity userEntity) {
        UserEntity result = userService.updateInfo(userEntity, AuthUtil.getUserId());
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 修改密码
     *
     * @param jsonObject 密码数据
     * @return String
     */
    @PostMapping("/updatePassword")
    @SaveLog(module = "个人信息", type = LogType.UPDATE)
    public String updatePassword(@RequestBody JSONObject jsonObject) {
        UserEntity result = userService.updatePassword(jsonObject, AuthUtil.getUserId());
        return result != null ? Result.data(result) : Result.fail(ResultCode.FAIL);
    }
}
