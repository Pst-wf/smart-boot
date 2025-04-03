package com.smart.file.utils;


import com.amazonaws.services.s3.model.S3Object;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.smart.common.constant.FileConstant;
import com.smart.common.utils.SpringUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.file.FileEntity;
import com.smart.entity.system.OssEntity;
import com.smart.model.exception.SmartException;
import com.smart.service.system.OssService;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传工具类
 *
 * @author wf
 * @version 1.0.0
 * @since 2022-07-26 00:00:00
 */
public class OssUtils {
    private static OssService ossService;

    private static OssService getOssService() {
        if (ossService == null) {
            ossService = SpringUtil.getBean(OssService.class);
        }
        return ossService;
    }

    /**
     * 文件上传
     *
     * @param key  文件唯一标识
     * @param file 文件对象
     * @return Map 返回结果
     */
    public static Map<String, String> upload(String key, MultipartFile file, OssEntity oss) throws Exception {
        Map<String, String> map = new HashMap<>(0);
        if (oss != null) {
            // 拼接 "/"
            if (StringUtil.isNotBlank(oss.getOssDir())) {
                if (!oss.getOssDir().endsWith("/")) {
                    oss.setOssDir(oss.getOssDir() + "/");
                }
            }
            switch (oss.getOssType()) {
                case FileConstant.OSS_TYPE_0:
                    // 本地上传
                    String bucket = oss.getBucket();
                    if (StringUtil.isNotBlank(bucket)) {
                        if (!bucket.endsWith("/")) {
                            bucket = bucket + "/";
                        }
                    }
                    map.put("key", bucket + key);
                    map.put("url", LocalUtils.upload(key, file, oss.getOssDir()));
                    break;
                case FileConstant.OSS_TYPE_1:
                    // minio
                    MinioUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ? oss.getOssDir() + key : key;
                    map.put("key", key);
                    map.put("url", MinioUtils.uploadInputStream(key, file.getInputStream()));
                    break;
                case FileConstant.OSS_TYPE_2:
                    // oss
                    AliOssUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ? oss.getOssDir() + key : "/" + key;
                    AliOssUtils.putObject(key, file.getInputStream());
                    map.put("key", key);
                    map.put("url", AliOssUtils.getObjectURL(key));
                    break;
                case FileConstant.OSS_TYPE_3:
                    break;
                case FileConstant.OSS_TYPE_4:
                    // 腾讯云cos
                    CosUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ? oss.getOssDir() + key : key;
                    CosUtils.putObject(key, file.getInputStream());
                    map.put("key", key);
                    map.put("url", CosUtils.getObjectURL(key));
                    break;
                default:
                    throw new SmartException("上传方式异常");
            }
        }
        return map;
    }

    /**
     * 文件上传(流)
     *
     * @param key 文件唯一标识
     * @param in  文件流
     * @return Map 返回结果
     */
    public static Map<String, String> upload(String key, InputStream in, OssEntity oss) throws Exception {
        Map<String, String> map = new HashMap<>(0);
        if (oss == null) {
            oss = getOssService().getCurrent();
        }
        if (oss != null) {
            switch (oss.getOssType()) {
                case FileConstant.OSS_TYPE_0:
                    String bucket = oss.getBucket();
                    if (StringUtil.isNotBlank(bucket)) {
                        if (!bucket.endsWith("/")) {
                            bucket = bucket + "/";
                        }
                    }
                    map.put("key", bucket + key);
                    map.put("url", LocalUtils.uploadInputStream(key, in, oss.getOssDir()));
                    break;
                case FileConstant.OSS_TYPE_1:
                    // minio
                    MinioUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ? oss.getOssDir() + "/" + key : key;
                    map.put("key", key);
                    map.put("url", MinioUtils.uploadInputStream(key, in));
                    break;
                case FileConstant.OSS_TYPE_2:
                    // oss
                    AliOssUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ?
                            oss.getOssDir() + "/" + key :
                            "/" + key;
                    AliOssUtils.putObject(key, in);
                    map.put("key", key);
                    map.put("url", AliOssUtils.getObjectURL(key));
                    break;
                case FileConstant.OSS_TYPE_3:
                    break;
                case FileConstant.OSS_TYPE_4:
                    // 腾讯云cos
                    CosUtils.init(oss);
                    key = StringUtil.isNotBlank(oss.getOssDir()) ? oss.getOssDir() + "/" + key : key;
                    CosUtils.putObject(key, in);
                    map.put("key", key);
                    map.put("url", CosUtils.getObjectURL(key));
                    break;
                default:
                    throw new SmartException("上传方式异常");
            }
        }
        return map;
    }

    /**
     * 删除文件
     *
     * @param list 要删除的问件
     */
    public static void delete(List<FileEntity> list) {
        for (FileEntity x : list) {
            if (StringUtil.isBlank(x.getUploadType())) {
                break;
            } else {
                OssEntity oss = Db.lambdaQuery(OssEntity.class).eq(OssEntity::getOssType, x.getUploadType()).one();
                if (oss == null) {
                    oss = getOssService().getCurrent();
                }
                if (oss != null) {
                    switch (oss.getOssType()) {
                        case FileConstant.OSS_TYPE_0:
                            LocalUtils.deleteFile(x.getFilePath());
                            break;
                        case FileConstant.OSS_TYPE_1:
                            // minio
                            MinioUtils.init(oss);
                            try {
                                MinioUtils.deleteFile(x.getFileKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case FileConstant.OSS_TYPE_2:
                            // oss
                            AliOssUtils.init(oss);
                            try {
                                AliOssUtils.removeObject(x.getFileKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case FileConstant.OSS_TYPE_3:
                            break;
                        case FileConstant.OSS_TYPE_4:
                            // 腾讯云cos
                            CosUtils.init(oss);
                            try {
                                CosUtils.removeObject(x.getFileKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        default:
                            throw new SmartException("上传方式异常");
                    }
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param fileEntity 文件对象
     * @return InputStream 文件流
     * @throws Exception 异常
     */
    public static InputStream download(FileEntity fileEntity) throws Exception {
        String uploadType = fileEntity.getUploadType();
        OssEntity oss;
        if (StringUtil.isBlank(uploadType)) {
            oss = getOssService().getCurrent();
        } else {
            oss = Db.lambdaQuery(OssEntity.class).eq(OssEntity::getOssType, uploadType).one();
        }
        if (oss != null) {
            switch (oss.getOssType()) {
                case FileConstant.OSS_TYPE_0:
                    return LocalUtils.getInputStream(fileEntity.getFilePath());
                case FileConstant.OSS_TYPE_1:
                    // minio
                    MinioUtils.init(oss);
                    return MinioUtils.download(fileEntity.getFileKey());
                case FileConstant.OSS_TYPE_2:
                    // oss
                    AliOssUtils.init(oss);
                    S3Object aliObject = AliOssUtils.getObject(fileEntity.getFileKey());
                    return aliObject.getObjectContent();
                case FileConstant.OSS_TYPE_3:
                    break;
                case FileConstant.OSS_TYPE_4:
                    // 腾讯云cos
                    CosUtils.init(oss);
                    S3Object cosObject = CosUtils.getObject(fileEntity.getFileKey());
                    return cosObject.getObjectContent();
                default:
                    throw new SmartException("上传方式异常");
            }
        }
        return null;
    }

    /**
     * 输入流生成文件
     *
     * @param in   输入流
     * @param path 文件路径
     * @return File
     * @throws IOException IO流异常
     */
    public static File InputStreamToFile(InputStream in, String path) throws IOException {
        InputStream initialStream = FileUtils.openInputStream(new File(path));
        File targetFile = new File(path);
        FileUtils.copyInputStreamToFile(initialStream, targetFile);
        return targetFile;
    }
}
