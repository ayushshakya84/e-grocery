package ru.voleshko.grocery.search.config.elastic;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.voleshko.grocery.search.service.index.ElasticIndexService;

@Component
@RequiredArgsConstructor
public class IndexCreator {

    private final ElasticIndexService elasticIndexService;
    private final ElasticProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void doOnStartUp() {
        createIndex();
    }

    @SneakyThrows
    private void createIndex() {
        // TODO: make human-readable mapping, use XContentBuilder builder or read from resource folder
        String mapping = "{\n" +
                "            \"properties\": {\n" +
                "                \"attributes\": {\n" +
                "                    \"type\": \"object\"\n" +
                "                },\n" +
                "                \"categories\": {\n" +
                "                    \"properties\": {\n" +
                "                        \"id\": {\n" +
                "                            \"type\": \"text\",\n" +
                "                            \"fields\": {\n" +
                "                                \"keyword\": {\n" +
                "                                    \"type\": \"keyword\",\n" +
                "                                    \"ignore_above\": 256\n" +
                "                                }\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"name\": {\n" +
                "                            \"type\": \"text\",\n" +
                "                            \"fields\": {\n" +
                "                                \"keyword\": {\n" +
                "                                    \"type\": \"keyword\",\n" +
                "                                    \"ignore_above\": 256\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"description\": {\n" +
                "                    \"type\": \"text\"\n" +
                "                },\n" +
                "                \"discountPercent\": {\n" +
                "                    \"type\": \"long\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"text\",\n" +
                "                    \"fields\": {\n" +
                "                        \"keyword\": {\n" +
                "                            \"type\": \"keyword\",\n" +
                "                            \"ignore_above\": 256\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"name\": {\n" +
                "                    \"type\": \"text\"\n" +
                "                },\n" +
                "                \"price\": {\n" +
                "                    \"type\": \"float\"\n" +
                "                },\n" +
                "                \"qty\": {\n" +
                "                    \"type\": \"long\"\n" +
                "                },\n" +
                "                \"weight\": {\n" +
                "                    \"type\": \"float\"\n" +
                "                }\n" +
                "            }\n" +
                "        }";
        elasticIndexService.createIndex(properties.getIndex().getName(), mapping);
    }
}
