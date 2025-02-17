package com.smart.file.utils;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件类型判断
 */
public class MimeTypeUtils {

    // 使用静态初始化器创建 Tika 单例实例
    private static final Tika tika = new Tika();

    /**
     * 根据文件的 MIME 类型判断文件是否为文本文件
     * 该方法首先检查文件是否有效，然后根据文件内容的前几KB检测文件的 MIME 类型，
     * 并判断是否以 "text/" 开头，以此来确定文件是否为文本文件
     *
     * @param file 要检查的文件对象
     * @return 如果文件是文本文件，则返回 true；否则返回 false
     */
    public static boolean isTextFileByMimeType(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        // 检查文件大小，防止处理超大文件
        if (file.length() > 50 * 1024 * 1024) { // 限制为50MB
            return false;
        }

        try {
            // 只读取文件的前几KB来检测 MIME 类型
            byte[] buffer = new byte[8192];
            int length = (int) Math.min(file.length(), buffer.length);
            try (InputStream inputStream = java.nio.file.Files.newInputStream(file.toPath())) {
                inputStream.read(buffer, 0, length);
            }
            String mimeType = tika.detect(buffer, file.getName());
            return mimeType.startsWith("text/");
        } catch (IOException e) {
            // 使用日志框架记录异常信息
            System.err.println("Error detecting MIME type: " + e.getMessage());
            return false;
        }
    }


    /**
     * 根据文件的 MIME 类型判断文件是否为文本文件
     * 该方法首先检查文件是否有效，然后根据文件内容的前几KB检测文件的 MIME 类型，
     * 并判断是否以 "text/" 开头，以此来确定文件是否为文本文件
     *
     * @param inputStream 要检查的文件流
     * @return 如果文件是文本文件，则返回 true；否则返回 false
     */
    public static boolean isTextFileByMimeType(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        try (InputStream is = inputStream) {
            String mimeType = tika.detect(is);
            return mimeType != null && mimeType.startsWith("text/");
        } catch (IOException e) {
            // 记录日志或进行其他错误处理
            System.err.println("Error detecting MIME type: " + e.getMessage());
            return false;
        }
    }
}
