package com.smart.file.utils;

import com.smart.common.utils.StringUtil;
import com.smart.model.exception.SmartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 服务器本地存储
 *
 * @author wf
 * @version 1.0.0
 * @since 2025-01-06
 */
public class LocalUtils {
    /**
     * 上传到服务器
     *
     * @param key        文件key
     * @param file       文件
     * @param uploadPath 存储路径
     * @return java.lang.String
     */
    public static String upload(String key, MultipartFile file, String uploadPath) {
        if (file == null || file.isEmpty()) {
            throw new SmartException("文件不能为空！");
        }
        if (StringUtil.isBlank(uploadPath)) {
            throw new SmartException("获取上传文件根目录失败！");
        }
        if (!uploadPath.endsWith("/")) {
            uploadPath = uploadPath + "/";
        }
        Path path = Paths.get(uploadPath + key);
        try {
            // 确保上传目录存在
            Files.createDirectories(path.getParent());
            // 将文件保存到本地文件系统
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path.toString();
    }

    /**
     * 上传流到服务器
     *
     * @param key        文件key
     * @param in         输入流
     * @param uploadPath 存储路径
     * @return java.lang.String
     */
    public static String uploadInputStream(String key, InputStream in, String uploadPath) {
        if (in == null) {
            throw new SmartException("文件流不能为空！");
        }
        if (StringUtil.isBlank(uploadPath)) {
            throw new SmartException("获取上传文件根目录失败！");
        }
        if (!uploadPath.endsWith("/")) {
            uploadPath = uploadPath + "/";
        }
        Path path = Paths.get(uploadPath + key);
        try {
            // 确保上传目录存在
            Files.createDirectories(path.getParent());
            // 将文件保存到本地文件系统
            Files.copy(in, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path.toString();
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取输入流
     *
     * @param path 文件路径
     */
    public static InputStream getInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new SmartException("获取文件失败！");
        }
    }
}
