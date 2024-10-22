package ru.voleshko.grocery.profile.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;
    private String adminUsername;
    private String adminPassword;
}
