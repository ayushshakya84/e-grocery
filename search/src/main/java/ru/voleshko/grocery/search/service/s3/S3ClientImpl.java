package ru.voleshko.grocery.search.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.voleshko.grocery.search.config.s3.S3Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ClientImpl implements S3Client {

    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;

    @PostConstruct
    public void init() {
        createBucket(s3Properties.getBuckets().getImageBucketName());
    }

    @Override
    public void putObject(String bucket, String key, ByteArrayInputStream file) {
        log.info("Sending file to bucket=[{}] with key=[{}]", bucket, key);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.available());

        amazonS3.putObject(bucket, key, file, metadata);
    }

    @Override
    public InputStream getObject(String bucket, String key) {
        try {
            return amazonS3.getObject(bucket, key).getObjectContent();
        } catch (AmazonS3Exception exception) {
            if (exception.getStatusCode() == 404) {
                throw new EntityNotFoundException(
                        "The object was not found in bucket " + bucket + " with key " + key
                );
            }
            throw exception;
        }
    }

    @Override
    public void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }
    }

}
