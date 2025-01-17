package com.smart.file.utils;


import com.amazonaws.util.IOUtils;
import com.smart.common.utils.ReflectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.OssEntity;
import com.smart.model.file.OssFile;
import io.minio.*;
import io.minio.credentials.Credentials;
import io.minio.http.Method;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.Date;

/**
 * Minio工具类
 *
 * @author wf
 */
public class MinioUtils {

    private static MinioClient client;
    /**
     * 默认minio参数
     */
    private static String ENDPOINT = "http://127.0.0.1:9000";
    private static String ACCESS_KEY = "minioadmin";
    private static String SECRET_KEY = "minioadmin";
    private static String BUCKET = "radish-oss";

    public static void init(OssEntity oss) {
        ENDPOINT = oss.getOssHost();
        ACCESS_KEY = oss.getAccessKey();
        SECRET_KEY = oss.getAccessSecret();
        BUCKET = oss.getBucket();
        if (client == null || checkChange()) {
            client = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY, SECRET_KEY).build();
        }
    }

    private static void init() {
        if (client == null || checkChange()) {
            client = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY, SECRET_KEY).build();
        }
    }

    /**
     * 验证是否需要重新初始化
     *
     * @return boolean
     */
    private static boolean checkChange() {
        okhttp3.HttpUrl baseUrl = ReflectUtil.getFieldValue(client, "baseUrl");
        io.minio.credentials.StaticProvider provider = ReflectUtil.getFieldValue(client, "provider");
        if (baseUrl != null) {
            String url = baseUrl.url().toString();
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            String endpoint = ENDPOINT;
            if (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }
            if (!url.equals(endpoint)) {
                return true;
            }
        }
        if (provider != null) {
            Credentials fetch = provider.fetch();
            if (fetch != null) {
                String key = fetch.accessKey();
                if (!StringUtil.notBlankAndEquals(key, ACCESS_KEY)) {
                    return true;
                }
                String secretKey = fetch.secretKey();
                return !StringUtil.notBlankAndEquals(secretKey, SECRET_KEY);
            }
        }
        return false;
    }

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只读
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";

    /**
     * 文件url前半段
     *
     * @param bucket 桶
     * @return 前半段
     */
    public static String getObjectPrefixUrl(String bucket) {
        return String.format("%s/%s/", ENDPOINT, bucket);
    }

    /**
     * 创建桶
     *
     * @param bucket 桶
     */
    public static void makeBucket(String bucket) throws Exception {
        if (client == null) {
            init();
        }
        // 判断桶是否存在
        boolean isExist = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!isExist) {
            // 新建桶
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 更新桶权限策略
     *
     * @param bucket 桶
     * @param policy 权限
     */
    public static void setBucketPolicy(String bucket, String policy) throws Exception {
        if (client == null) {
            init();
        }
        switch (policy) {
            case "read-only":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "write-only":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "read-write":
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "none":
            default:
                break;
        }
    }

    /**
     * 上传本地文件
     *
     * @param bucket    桶
     * @param objectKey 文件key
     * @param filePath  文件路径
     * @return 文件url
     */
    public static String uploadFile(String bucket, String objectKey, String filePath) throws Exception {
        if (client == null) {
            init();
        }
        client.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectKey).filename(filePath).contentType("image/png").build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 流式上传文件
     *
     * @param objectKey   文件key
     * @param inputStream 文件输入流
     * @return 文件url
     */
    public static String uploadInputStream(String objectKey, InputStream inputStream) throws Exception {
        return uploadInputStream(objectKey, inputStream, "application/octet-stream");
    }

    /**
     * 流式上传文件
     *
     * @param objectKey   文件key
     * @param inputStream 文件输入流
     * @return 文件url
     */
    public static String uploadInputStream(String objectKey, InputStream inputStream, String contentType) throws Exception {
        if (client == null) {
            init();
        }
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        client.putObject(PutObjectArgs.builder().bucket(BUCKET).object(objectKey).stream(byteArrayInputStream, byteArrayInputStream.available(), -1).contentType(contentType).build());
        return getObjectPrefixUrl(BUCKET) + objectKey;
    }

    /**
     * 下载文件
     *
     * @param objectKey 文件key
     * @return 文件流
     */
    public static InputStream download(String objectKey) throws Exception {
        if (client == null) {
            init();
        }
        return client.getObject(GetObjectArgs.builder().bucket(BUCKET).object(objectKey).build());
    }

    /**
     * 文件复制
     *
     * @param sourceBucket    源桶
     * @param sourceObjectKey 源文件key
     * @param bucket          桶
     * @param objectKey       文件key
     * @return 新文件url
     */
    public static String copyFile(String sourceBucket, String sourceObjectKey, String bucket, String objectKey) throws Exception {
        if (client == null) {
            init();
        }
        CopySource source = CopySource.builder().bucket(sourceBucket).object(sourceObjectKey).build();
        client.copyObject(CopyObjectArgs.builder().bucket(bucket).object(objectKey).source(source).build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 删除文件
     *
     * @param objectKey 文件key
     */
    public static void deleteFile(String objectKey) throws Exception {
        if (client == null) {
            init();
        }
        client.removeObject(RemoveObjectArgs.builder().bucket(BUCKET).object(objectKey).build());
    }

    /**
     * 获取文件签名url
     *
     * @param bucket    桶
     * @param objectKey 文件key
     * @param expires   签名有效时间  单位秒
     * @return 文件签名地址
     */
    public static String getSignedUrl(String bucket, String objectKey, int expires) throws Exception {
        if (client == null) {
            init();
        }
        return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(objectKey).expiry(expires).build());
    }

    /**
     * 获取文件信息
     *
     * @param objectKey 文件key
     * @return OssFile
     */
    public OssFile statFile(String objectKey) {
        try {
            if (client == null) {
                init();
            }
            StatObjectResponse stat = client.statObject(StatObjectArgs.builder().bucket(BUCKET).object(objectKey).build());
            OssFile ossFile = new OssFile();
            ossFile.setName(StringUtils.isBlank(stat.object()) ? objectKey : stat.object());
            ossFile.setLink(this.fileLink(ossFile.getName()));
            ossFile.setHash(String.valueOf(stat.hashCode()));
            ossFile.setLength(stat.size());
            ossFile.setPutTime(Date.from(stat.lastModified().toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()));
            ossFile.setContentType(stat.contentType());
            return ossFile;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String fileLink(String fileName) {
        return ENDPOINT.concat("/").concat(BUCKET).concat("/").concat(fileName);
    }

}
