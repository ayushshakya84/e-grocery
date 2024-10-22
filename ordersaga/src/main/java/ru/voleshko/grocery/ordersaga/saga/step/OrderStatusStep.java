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

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusStep implements SagaStep {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    @Transactional
    public void doStep(Order order) {
        log.info("Sending request for changing order status with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "orderExchange",
                "orderStatusRequestQueue",
                objectMapper.writeValueAsString(order)
        ));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void handleError(Order order) {
        log.info("Handling error of changing order status with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "orderExchange",
                "orderStatusRollbackQueue",
                objectMapper.writeValueAsString(order)
        ));
    }
}
