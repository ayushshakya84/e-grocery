package ru.voleshko.grocery.profile.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import ru.voleshko.grocery.profile.ProfileApplication;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final Predicate<RequestHandler> apiSelector =
            RequestHandlerSelectors.basePackage(ProfileApplication.class.getPackage().getName());
    private static final String JWT_AUTH_NAME = "JWT";

    @Bean
    public Docket v1Api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("v1")
                .select()
                .apis(apiSelector::test)
                .build()
                .securitySchemes(apiKey());
    }

    private List<ApiKey> apiKey() {
        return singletonList(new ApiKey(JWT_AUTH_NAME, HttpHeaders.AUTHORIZATION, "header"));
    }
}
