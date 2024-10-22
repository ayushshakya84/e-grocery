package ru.voleshko.grocery.search.config.elastic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "elastic")
public class ElasticProperties {

    private String host;
    private int port;
    private String scheme;
    private Index index;

    @Setter
    @Getter
    public static class Index {

        private String name;
    }

}
