package ru.voleshko.grocery.order.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.voleshko.grocery.order.OrderApplication;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private final Predicate<RequestHandler> fullApiSelector =
            RequestHandlerSelectors.basePackage(OrderApplication.class.getPackage().getName());

    @Bean
    public Docket v1Api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("v1")
                .select()
                .apis(fullApiSelector::test)
                .build();
    }

}
