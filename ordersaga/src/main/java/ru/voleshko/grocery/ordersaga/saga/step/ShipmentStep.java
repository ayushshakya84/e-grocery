package ru.voleshko.grocery.ordersaga.saga.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.ordersaga.dto.Order;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentStep implements SagaStep {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    @Transactional
    public void doStep(Order order) {
        log.info("Sending request for making shipment for order with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "shipmentExchange",
                "shipmentRequestQueue",
                objectMapper.writeValueAsString(order)
        ));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void handleError(Order order) {
        log.info("Handling error of shipment for order with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "shipmentExchange",
                "shipmentRollbackQueue",
                objectMapper.writeValueAsString(order)
        ));
    }
}
