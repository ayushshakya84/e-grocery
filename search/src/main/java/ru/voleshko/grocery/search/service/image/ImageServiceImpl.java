package ru.voleshko.grocery.search.service.image;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.voleshko.grocery.search.config.s3.S3Properties;
import ru.voleshko.grocery.search.service.s3.S3Client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public void saveImage(byte[] image, @NonNull UUID id) {
        s3Client.putObject(bucket(), id.toString(), new ByteArrayInputStream(image));
    }

    @Override
    public InputStream getImage(@NonNull UUID key) {
        return s3Client.getObject(bucket(), key.toString());
    }

    private String bucket() {
        return s3Properties.getBuckets().getImageBucketName();
    }
}
