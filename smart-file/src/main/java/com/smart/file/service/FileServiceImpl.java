package com.smart.file.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.smart.common.constant.FileConstant;
import com.smart.common.utils.*;
import com.smart.entity.file.FileEntity;
import com.smart.entity.file.FileRecordEntity;
import com.smart.entity.system.OssEntity;
import com.smart.file.dao.FileDao;
import com.smart.file.utils.OssUtils;
import com.smart.model.exception.SmartException;
import com.smart.model.file.DocumentCallBackVO;
import com.smart.mybatis.service.impl.BaseServiceImpl;
import com.smart.service.file.FileRecordService;
import com.smart.service.file.FileService;
import com.smart.service.system.ConfigService;
import com.smart.service.system.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.smart.common.constant.FileTypeConstant.CALLBACK_STATUS_6;
import static com.smart.common.constant.SmartConstant.*;


/**
 * 文件中心 ServiceImpl
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
@Slf4j
@Service("fileService")
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl extends BaseServiceImpl<FileDao, FileEntity> implements FileService {
    @Autowired
    @Qualifier("documentTypeMap")
    private Map<String, String> documentTypeMap;
    @Autowired
    FileRecordService fileRecordService;
    @Autowired
    ConfigService configService;
    @Autowired
    OssService ossService;

    /**
     * 上传文件
     *
     * @param files 文件数组
     * @param auto  是否自动填充
     * @return list
     * @throws Exception 异常
     */
    @Override
    public List<FileEntity> upload(List<MultipartFile> files, boolean auto) throws Exception {
        List<FileEntity> fileList = new ArrayList<>();
        if (ListUtil.isEmpty(files)) {
            throw new SmartException("上传的文件为空！");
        }
        Date date = new Date();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (StringUtil.isBlank(fileName)) {
                throw new SmartException("获取文件名失败！");
            }
            // 获取文件名及后缀
            String name = FilenameUtils.getBaseName(fileName);
            String suffix = FilenameUtils.getExtension(fileName);

            // 获取随机数
            String randomStr = StringUtil.getRandomStr(6);
            String dateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            // 生成文件key
            String key = name + randomStr + dateStr + "." + suffix;
            OssEntity current = ossService.getCurrent();
            Map<String, String> map = OssUtils.upload(key, file, current);
            String url = map.get("url");
            key = map.get("key");
            FileEntity fileEntity = new FileEntity();
            if (StringUtil.isNotBlank(url)) {
                fileEntity.setFilePath(url);
                fileEntity.setFileKey(key);
                fileEntity.setFileName(fileName);
                fileEntity.setFileSize(new BigDecimal(file.getSize()));
                fileEntity.setIsEditable(StringUtil.isNotBlank(documentTypeMap.get(suffix)) ? YES : NO);
                fileEntity.setContentType(file.getContentType());
                fileEntity.setUploadType(current.getOssType());
                fileEntity.setAuto(auto);
                if (!auto) {
                    fileEntity.setCreateBy(SYSTEM_ID);
                    fileEntity.setUpdateBy(SYSTEM_ID);
                    fileEntity.setIsDeleted(NO);
                    fileEntity.setCreateDate(date);
                    fileEntity.setUpdateDate(date);
                }
                fileList.add(fileEntity);
            }
        }
        boolean b = super.saveBatch(fileList);
        if (b) {
            List<FileRecordEntity> recordList = new ArrayList<>();
            // 筛选可编辑文件
            List<FileEntity> collect = fileList.stream().filter(item -> item.getIsEditable().equals(YES)).collect(Collectors.toList());
            // 保存记录表
            collect.forEach(item -> {
                FileRecordEntity record = new FileRecordEntity();
                BeanUtil.copyProperties(item, record);
                record.setId(null);
                record.setFileId(item.getId());
                recordList.add(record);
            });
            if (ListUtil.isNotEmpty(recordList)) {
                fileRecordService.saveBatch(recordList);
            }
            return fileList;
        }
        return null;
    }

    /**
     * 上传本地文件
     *
     * @param files 文件数组
     * @return list
     * @throws Exception 异常
     */
    @Override
    public List<FileEntity> uploadLocal(List<File> files) throws Exception {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        if (ListUtil.isNotEmpty(files)) {
            for (File file : files) {
                multipartFiles.add(getMultipartFile(file));
            }
        }
        return upload(multipartFiles, true);
    }

    @Override
    public boolean delete(FileEntity entity) {
        try {
            List<FileEntity> removeList = super.getListByIdList(entity.getDeleteIds());
            // todo oss 删除
            boolean b = super.delete(entity);
            if (b) {
                OssUtils.delete(removeList);
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SmartException("删除文件失败");
        }
    }

    /**
     * 下载
     *
     * @param entity 文件bean
     * @return InputStream
     * @throws Exception 异常
     */
    @Override
    public InputStream download(FileEntity entity) throws Exception {
        if (null == entity) {
            throw new SmartException("下载失败，文件为空！");
        }
        return OssUtils.download(entity);
    }

    /**
     * 下载
     *
     * @param id 文件ID
     * @return InputStream
     * @throws Exception 异常
     */
    @Override
    public InputStream downloadById(String id) throws Exception {
        FileEntity one = super.getById(id);
        if (one == null) {
            return null;
        }
        return download(one);
    }

    /**
     * 批量下载
     *
     * @param fileEntity 文件
     * @param response   响应
     */
    @Override
    public void downloadSingle(FileEntity fileEntity, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = download(fileEntity);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            response.addHeader("Content-Disposition", "attachment; filename=" + EncodeUtil.encodeUrl(fileEntity.getFileName()));
            response.setContentType("application/octet-stream; charset=UTF-8");
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
     * 批量下载
     *
     * @param list 文件集合
     */
    @Override
    public void downloadBatch(List<FileEntity> list, HttpServletResponse response) {
        ZipOutputStream zip = null;
        response.setHeader("Content-Disposition", "attachment; filename=" + EncodeUtil.encodeUrl("下载文件.zip"));
        response.setContentType("application/octet-stream; charset=UTF-8");
        try {
            zip = new ZipOutputStream(response.getOutputStream());
            resetFileName(list);
            for (FileEntity fileEntity : list) {
                InputStream in = download(fileEntity);
                //压缩文件名称 设置ZipEntry对象
                zip.putNextEntry(new ZipEntry(fileEntity.getFileName()));
                int temp;
                // 读取内容
                while ((temp = in.read()) != -1) {
                    // 压缩输出
                    zip.write(temp);
                }
                in.close(); // 关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 关闭流
            try {
                if (null != zip) {
                    zip.flush();
                    zip.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 校验文件名称并重命名
     */
    private void resetFileName(List<FileEntity> list) {
        List<String> uniqueList = list.stream().collect(Collectors.groupingBy(FileEntity::getFileName, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey).collect(Collectors.toList());
        Map<String, Integer> map = new HashMap<>(0);
        list.forEach(item -> {
            if (uniqueList.contains(item.getFileName())) {
                if (map.containsKey(item.getFileName())) {
                    item.setFileName(item.getFileName() + (map.get(item.getFileName()) + 1));
                } else {
                    item.setFileName(item.getFileName());
                    map.put(item.getFileName(), 0);
                }
            }
        });
    }

    /**
     * 处理文件转换格式
     *
     * @param file     文件
     * @param type     转换类型
     * @param response 响应
     */
    @Override
    public void format(MultipartFile file, String type, HttpServletResponse response) {
    }

    /**
     * 在线编辑
     *
     * @param request  请求
     * @param response 响应
     * @param id       文件ID
     */
    @Override
    public void online(HttpServletRequest request, HttpServletResponse response, String id) {
        FileEntity fileEntity = super.get(id);
        if (fileEntity == null) {
            throw new SmartException("文件不存在！");
        }
        if (StringUtils.isBlank(fileEntity.getFileName()) || !fileEntity.getFileName().contains(".")) {
            throw new SmartException("文件格式异常！");
        }
        String suffix = documentTypeMap.get(fileEntity.getFileName().substring(fileEntity.getFileName().lastIndexOf(".") + 1));
        String url = configService.getConfig("file_document_url") + "?" +
                "callback=" + EncodeUtil.encodeUrl(configService.getConfig("file_document_callback_url"), StandardCharsets.UTF_8.toString()) +
                "&url=" + EncodeUtil.encodeUrl(fileEntity.getFilePath(), StandardCharsets.UTF_8.toString()) +
                "&fileName=" + EncodeUtil.encodeUrl(fileEntity.getFileName(), StandardCharsets.UTF_8.toString()) +
                "&documentType=" + suffix +
                "&id=" + id;
        log.info("在线编辑重定向 -> {}", url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取编辑参数
     *
     * @param id 文件ID
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject onlineModal(String id) {
        FileEntity fileEntity = super.get(id);
        if (fileEntity == null) {
            throw new SmartException("文件不存在！");
        }
        if (StringUtils.isBlank(fileEntity.getFileName()) || !fileEntity.getFileName().contains(".")) {
            throw new SmartException("文件格式异常！");
        }
        String suffix = documentTypeMap.get(fileEntity.getFileName().substring(fileEntity.getFileName().lastIndexOf(".") + 1));
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("callback", configService.getConfig("file_document_callback_url"));
        // 是否本地上传
        boolean isLocal = StringUtil.notBlankAndEquals(fileEntity.getUploadType(), FileConstant.OSS_TYPE_0);
        if (isLocal) {
            String url = configService.getConfig("file_document_local_url");
            if (StringUtil.isNotBlank(url)) {
                if (!url.endsWith("/")) {
                    url += "/";
                }
                jsonObject.put("url", url + fileEntity.getFileKey());
            } else {
                jsonObject.put("url", fileEntity.getFileKey());
            }
        } else {
            jsonObject.put("url", fileEntity.getFilePath());
        }
        jsonObject.put("serverUrl", configService.getConfig("file_document_url"));
        jsonObject.put("fileName", fileEntity.getFileName());
        jsonObject.put("documentType", suffix);
        jsonObject.put("id", id);
        jsonObject.put("isLocal", isLocal);
        log.info("获取编辑参数 -> {}", jsonObject.toJSONString());
        return jsonObject;
    }

    /**
     * 回调
     *
     * @param vo 回调模型 (在线编辑)
     * @param id 文件基础表唯一标识
     * @throws Exception 异常
     */
    @Override
    public void callback(DocumentCallBackVO vo, String id) throws Exception {
        if (vo.getStatus().equals(CALLBACK_STATUS_6)) {
            if (StrUtil.isNotBlank(vo.getUrl())) {
                log.info("回调返回文件地址 - {}", vo.getUrl());
                FileEntity fileEntity = super.get(id);
                if (fileEntity == null) {
                    throw new SmartException("文件不存在！");
                }
                OssEntity current = ossService.getOne(new LambdaQueryWrapper<OssEntity>().eq(OssEntity::getOssType, fileEntity.getUploadType()));
                // 保存逻辑
                URL netUrl = new URL(vo.getUrl());
                InputStream inputStream = netUrl.openStream();
                BigDecimal fileSize = new BigDecimal(inputStream.available());

                // 获取文件名及后缀
                String name = FilenameUtils.getBaseName(fileEntity.getFileName());
                String suffix = FilenameUtils.getExtension(fileEntity.getFileName());

                // 获取随机数
                String randomStr = StringUtil.getRandomStr(6);
                String dateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
                // 生成文件key
                String key = name + randomStr + dateStr + "." + suffix;
                Map<String, String> map = OssUtils.upload(key, inputStream, current);
                String url = map.get("url");
                key = map.get("key");
                if (StringUtil.isNotBlank(url)) {
                    fileEntity.setFileSize(fileSize);
                    fileEntity.setFilePath(url);
                    fileEntity.setFileKey(key);
                    boolean b = super.updateById(fileEntity);
                    if (!b) {
                        throw new SmartException("更新文件版本失败！");
                    }
                    FileRecordEntity record = new FileRecordEntity();
                    BeanUtil.copyProperties(fileEntity, record);
                    record.setId(null);
                    record.setFileId(fileEntity.getId());
                    FileRecordEntity result = fileRecordService.saveEntity(record);
                    if (result == null) {
                        throw new SmartException("更新文件版本失败！");
                    }
                }
            }
        }
    }

    /**
     * File 转 MultipartFile
     *
     * @param file 文件
     * @return MultipartFile
     */
    private MultipartFile getMultipartFile(File file) {
        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }

    /**
     * 通过模板生成本地文件
     *
     * @param dataModel    数据源
     * @param templatePath 模板地址
     * @param filePath     输出文件地址
     * @return String
     */
    @Override
    public String createByTemplate(Map<String, Object> dataModel, String templatePath, String filePath) {
        //读取模板
        XWPFTemplate template;
        //判断config是否为空，不为空表示有关联表
        Configure config = (Configure) dataModel.get("config");
        InputStream in;
        try {
            Resource resource = new ClassPathResource(templatePath);
            in = resource.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SmartException("获取模板文件失败！");
        }
        if (config == null) {
            template = XWPFTemplate.compile(in);
        } else {
            template = XWPFTemplate.compile(in, config);
        }
        template.render(dataModel);
        // 生成文件
        FileOutputStream out;
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new SmartException("输出文件目录创建出错！");
        }
        try {
            out = new FileOutputStream(file);
            template.write(out);
            out.flush();
            out.close();
            template.close();
            List<FileEntity> fileEntities = uploadLocal(ListUtil.newArrayList(file));
            if (ListUtil.isEmpty(fileEntities)) {
                throw new SmartException("上传本地文件到文件服务器出错！");
            }
            return fileEntities.get(0).getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (file.exists()) {
                file.delete();
            }
            if (parentFile.exists()) {
                parentFile.delete();
            }
        }
    }

    /**
     * 克隆文件
     *
     * @param fileIds  文件IDS
     * @param filePath 输出文件地址
     * @return String
     */
    @Override
    public List<FileEntity> cloneFile(List<String> fileIds, String filePath) {
        List<FileEntity> list = super.list(new LambdaQueryWrapper<FileEntity>().in(FileEntity::getId, fileIds));
        List<File> fileList = new ArrayList<>();
        try {
            for (FileEntity fileEntity : list) {
                String fileName = fileEntity.getFileName();
                InputStream in = download(fileEntity);
                File file = new File(filePath + fileName);
                FileUtils.copyInputStreamToFile(in, file);
                fileList.add(file);
            }
            return uploadLocal(fileList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 上传成功后删除
            fileList.forEach(File::delete);
        }
    }

}

