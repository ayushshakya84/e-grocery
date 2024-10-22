package ru.voleshko.grocery.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.voleshko.grocery.product.domain.repository.ProductRepository;
import ru.voleshko.grocery.product.rest.v1.converter.product.ProductConverter;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductDto;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

/**
 * To make test data appear in Search Service, we have to explicitly send events to it
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.liquibase.contexts", havingValue = "dev")
public class TestDataCreator {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void doOnStartUp() {
        sendProductCatalogueToSearchService();
    }

    private void sendProductCatalogueToSearchService() {
        productRepository.findAll().forEach(product -> {
            log.info("Sending test data for indexing to Search Service: [{}]", product.getName());
            ProductDto dto = productConverter.fromDomain(product);
            try {
                outboxRepository.save(new OutboxMessage(
                        "productExchange", "productUpsertedQueue", objectMapper.writeValueAsString(dto))
                );
            } catch (Exception e) {
                log.error("Failed to start application in dev mode. Was not able to send data to Search Service " +
                        "for product {} : ", product.getName(), e);
                System.exit(-1);
            }
        });
    }
}
