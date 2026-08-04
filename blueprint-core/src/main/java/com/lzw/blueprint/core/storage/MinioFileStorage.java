package com.lzw.blueprint.core.storage;

import com.lzw.blueprint.core.config.FileProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "file.upload.storage", havingValue = "minio")
public class MinioFileStorage implements FileStorage {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioFileStorage(FileProperties properties) {
        FileProperties.Minio minioProps = properties.getMinio();
        this.minioClient = MinioClient.builder()
                .endpoint(minioProps.getEndpoint())
                .credentials(minioProps.getAccessKey(), minioProps.getSecretKey())
                .build();
        this.bucket = minioProps.getBucket();
    }

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO 初始化失败", e);
        }
    }

    @Override
    public String store(MultipartFile file, String storedName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(storedName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return getUrl(storedName);
        } catch (Exception e) {
            throw new RuntimeException("文件上传到 MinIO 失败", e);
        }
    }

    @Override
    public void delete(String storedName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(storedName).build());
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public String getUrl(String storedName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(storedName)
                            .method(Method.GET)
                            .expiry(1, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getType() {
        return "MINIO";
    }
}