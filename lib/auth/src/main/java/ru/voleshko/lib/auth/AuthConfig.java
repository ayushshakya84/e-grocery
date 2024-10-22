package ru.voleshko.lib.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AuthConfig.class)
public class AuthConfig {

    @Bean
    UserService userService() {
        return new UserServiceImpl(new ObjectMapper());
    }
}
