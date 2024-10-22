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
public class ReservationStep implements SagaStep {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    @Transactional
    public void doStep(Order order) {
        log.info("Sending request for making reservation for order with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "reservationExchange",
                "reservationRequestQueue",
                objectMapper.writeValueAsString(order)
        ));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void handleError(Order order) {
        log.info("Handling error of reservation for order with id=[{}]", order.getId());
        outboxRepository.save(new OutboxMessage(
                "reservationExchange",
                "reservationRollbackQueue",
                objectMapper.writeValueAsString(order)
        ));
    }
}
