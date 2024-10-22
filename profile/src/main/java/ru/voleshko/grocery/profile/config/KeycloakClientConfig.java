package ru.voleshko.grocery.profile.config;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakClientConfig {

    private final KeycloakProperties props;

    @Bean
    public Keycloak keycloakClient () {
         return KeycloakBuilder.builder()
                 .serverUrl(props.getServerUrl() + "/auth")
                 .username(props.getAdminUsername())
                 .password(props.getAdminPassword())
                 .grantType(OAuth2Constants.PASSWORD)
                 .realm("master")
                 .clientId("admin-cli")
                 .resteasyClient(
                         new ResteasyClientBuilder()
                                 .connectionPoolSize(10).build()
                 ).build();
    }
}
