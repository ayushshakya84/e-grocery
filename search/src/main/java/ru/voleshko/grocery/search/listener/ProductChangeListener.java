package ru.voleshko.grocery.search.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.voleshko.grocery.search.config.elastic.ElasticProperties;
import ru.voleshko.grocery.search.service.index.ElasticIndexService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductChangeListener {

    private final ElasticIndexService indexService;
    private final ElasticProperties properties;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "productUpsertedQueue")
    public void upsertProduct(String product) throws JsonProcessingException {
        String id = objectMapper.readTree(product).get("id").asText();
        indexService.index(properties.getIndex().getName(), id, product);
    }

    @RabbitListener(queues = "productDeletedQueue")
    public void deleteProduct(String productId) {
        indexService.delete(properties.getIndex().getName(), productId);
    }
}
