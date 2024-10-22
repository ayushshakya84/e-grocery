package ru.voleshko.grocery.search.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {

    private URI url;

    private String region;

    private String accessKey;

    private String secretKey;

    private Buckets buckets;

    @Setter
    @Getter
    public static class Buckets {

        private String imageBucketName;
    }
}
