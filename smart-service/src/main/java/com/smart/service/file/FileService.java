package com.smart.service.file;

import com.alibaba.fastjson.JSONObject;
import com.smart.entity.file.FileEntity;
import com.smart.model.file.DocumentCallBackVO;
import com.smart.mybatis.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件中心 Service
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public interface FileService extends BaseService<FileEntity> {

    /**
     * 上传文件
     *
     * @param files 文件数组
     * @param auto  是否自动填充
     * @return list
     * @throws Exception 异常
     */
    List<FileEntity> upload(List<MultipartFile> files, boolean auto) throws Exception;

    /**
     * 上传本地文件
     *
     * @param files 文件数组
     * @return list
     * @throws Exception 异常
     */
    List<FileEntity> uploadLocal(List<File> files) throws Exception;

    /**
     * 下载
     *
     * @param entity 文件bean
     * @return InputStream
     * @throws Exception 异常
     */
    InputStream download(FileEntity entity) throws Exception;

    /**
     * 下载
     *
     * @param id 文件ID
     * @return InputStream
     * @throws Exception 异常
     */
    InputStream downloadById(String id) throws Exception;

    /**
     * 批量下载
     *
     * @param fileEntity 文件
     * @param response   响应
     */
    void downloadSingle(FileEntity fileEntity, HttpServletResponse response);

    /**
     * 批量下载
     *
     * @param list     文件集合
     * @param response 响应
     */
    void downloadBatch(List<FileEntity> list, HttpServletResponse response);

    /**
     * 处理文件转换格式
     *
     * @param file     文件
     * @param type     转换类型
     * @param response 响应
     */
    void format(MultipartFile file, String type, HttpServletResponse response);

    /**
     * 在线编辑
     *
     * @param request  请求
     * @param response 响应
     * @param id       文件ID
     */
    void online(HttpServletRequest request, HttpServletResponse response, String id);

    /**
     * 回调
     *
     * @param vo 回调模型 (在线编辑)
     * @param id 文件基础表唯一标识
     * @throws Exception 异常
     */
    void callback(DocumentCallBackVO vo, String id) throws Exception;

    /**
     * 通过模板生成本地文件
     *
     * @param dataModel    数据源
     * @param templatePath 模板地址
     * @param filePath     输出文件地址
     * @return String
     */
    String createByTemplate(Map<String, Object> dataModel, String templatePath, String filePath);

    /**
     * 克隆文件
     *
     * @param fileIds  文件IDS
     * @param filePath 输出文件地址
     * @return String
     */
    List<FileEntity> cloneFile(List<String> fileIds, String filePath) throws Exception;

    /**
     * 获取编辑参数
     *
     * @param id       文件ID
     * @return com.alibaba.fastjson.JSONObject
     */
    JSONObject onlineModal(String id);
}

