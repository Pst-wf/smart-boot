package com.smart.file.utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.smart.common.utils.ReflectUtil;
import com.smart.common.utils.StringUtil;
import com.smart.entity.system.OssEntity;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * aws-s3 通用存储操作 支持所有兼容s3协议的云存储: {阿里云OSS，腾讯云COS，七牛云，京东云，minio 等}
 *
 * @since 2024/05/25
 * @since 1.0
 */
@RequiredArgsConstructor
public class CosUtils {
    private static AmazonS3 amazonS3;
    private static String BUCKET = "guanjunlingpao-1327999465";

    public static void init(OssEntity oss) {
        BUCKET = oss.getBucket();
        if (amazonS3 == null || checkChange(oss)) {
            String endpoint = oss.getOssHost();
            String accessKey = oss.getAccessKey();
            String secretKey = oss.getAccessSecret();
            String region = oss.getRegion();
            System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
            ClientConfiguration clientConfiguration = new ClientConfiguration();//初始化客户端config
            clientConfiguration.setMaxConnections(100);
            clientConfiguration.setProtocol(Protocol.HTTPS);//设置mos 请求协议为http
            clientConfiguration.setSignerOverride("S3SignerType");
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                    endpoint, region);//创建AWS凭证
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
            amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
                    .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
                    .disableChunkedEncoding().withPathStyleAccessEnabled(false).build();
        }
    }

    /**
     * 验证是否需要重新初始化
     *
     * @return boolean
     */
    private static boolean checkChange(OssEntity oss) {
        java.net.URI url = ReflectUtil.getFieldValue(amazonS3, "endpoint");
        AWSStaticCredentialsProvider awsCredentialsProvider = ReflectUtil.getFieldValue(amazonS3, "awsCredentialsProvider");
        if (url != null) {
            if (!url.toString().equals(oss.getOssHost())) {
                return true;
            }
        }
        if (awsCredentialsProvider != null) {
            AWSCredentials credentials = awsCredentialsProvider.getCredentials();
            if (credentials != null) {
                String awsAccessKeyId = credentials.getAWSAccessKeyId();
                if (!StringUtil.notBlankAndEquals(awsAccessKeyId, oss.getAccessKey())) {
                    return true;

                }
                String awsSecretKey = credentials.getAWSSecretKey();
                return !StringUtil.notBlankAndEquals(awsSecretKey, oss.getAccessSecret());

            }
        }
        return false;
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public static void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket((bucketName));
        }
    }

    /**
     * 获取全部bucket
     * <p>
     *
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public static List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }

    /**
     * @param bucketName bucket名称
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public static Optional<Bucket> getBucket(String bucketName) {
        return amazonS3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
    }

    /**
     * @param bucketName bucket名称
     * @see <a href=
     * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteBucket">AWS API
     * Documentation</a>
     */
    @SneakyThrows
    public static void removeBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName);
    }

    /**
     * 根据文件前置查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @param recursive  是否递归查询
     * @return S3ObjectSummary 列表
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public static List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
        return new ArrayList<>(objectListing.getObjectSummaries());
    }

    /**
     * 获取文件外链
     *
     * @param objectName 文件名称
     * @return url
     * @see AmazonS3#generatePresignedUrl(String bucketName, String key, Date expiration)
     */
    @SneakyThrows
    public static String getObjectURL(String objectName) {
        URL url = amazonS3.getUrl(BUCKET, objectName);
        return url.toString();
    }

    /**
     * 获取文件
     *
     * @param objectName 文件名称
     * @return 二进制流
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
     * API Documentation</a>
     */
    @SneakyThrows
    public static S3Object getObject(String objectName) {
        return amazonS3.getObject(BUCKET, objectName);
    }

    /**
     * 上传文件
     *
     * @param objectName 文件名称
     * @param bytes      文件
     */
    public static PutObjectResult putObject(String objectName, byte[] bytes) {
        return putObject(objectName, bytes, bytes.length, "application/octet-stream");
    }

    /**
     * 上传文件
     *
     * @param objectName  文件名称
     * @param bytes       文件
     * @param size        大小
     * @param contextType 类型
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject">AWS
     * API Documentation</a>
     */
    public static PutObjectResult putObject(String objectName, byte[] bytes, long size, String contextType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contextType);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 上传
        return amazonS3.putObject(BUCKET, objectName, byteArrayInputStream, objectMetadata);

    }

    /**
     * 上传文件
     *
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws Exception 异常
     */
    public static PutObjectResult putObject(String objectName, InputStream stream) throws Exception {
        byte[] bytes = IOUtils.toByteArray(stream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return putObject(objectName, byteArrayInputStream, byteArrayInputStream.available(), "application/octet-stream");
    }

    /**
     * 上传文件
     *
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contextType 类型
     * @throws Exception 异常
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject">AWS
     * API Documentation</a>
     */
    public static PutObjectResult putObject(String objectName, InputStream stream, long size,
                                            String contextType) throws Exception {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contextType);
        // 上传
        return amazonS3.putObject(BUCKET, objectName, stream, objectMetadata);

    }

    /**
     * 获取文件信息
     *
     * @param objectName 文件名称
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
     * API Documentation</a>
     */
    public static S3Object getObjectInfo(String objectName) throws Exception {
        @Cleanup
        S3Object object = amazonS3.getObject(BUCKET, objectName);
        return object;
    }

    /**
     * 删除文件
     *
     * @param objectName 文件名称
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteObject">AWS API Documentation</a>
     */
    public static void removeObject(String objectName) {
        amazonS3.deleteObject(BUCKET, objectName);
    }


}
