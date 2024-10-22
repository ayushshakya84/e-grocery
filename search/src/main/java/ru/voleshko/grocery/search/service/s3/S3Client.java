package ru.voleshko.grocery.search.service.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface S3Client {


    void putObject(String bucket, String key, ByteArrayInputStream inputStream);

    InputStream getObject(String bucket, String key);

    void createBucket(String bucketName);
}
