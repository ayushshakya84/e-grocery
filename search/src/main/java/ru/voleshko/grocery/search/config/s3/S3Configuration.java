package ru.voleshko.grocery.search.config.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Configuration {

    private final S3Properties properties;

    @Bean
    public AmazonS3 amazonS3() {
        ClientConfiguration clientConfig = new ClientConfiguration()
                .withMaxConnections(100)
                .withMaxErrorRetry(15)
                .withConnectionTimeout(1_000)
                .withSocketTimeout(1_000);

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        properties.getUrl().toString(),
                        properties.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        properties.getAccessKey(),
                        properties.getSecretKey())))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfig)
                .build();
    }
}
