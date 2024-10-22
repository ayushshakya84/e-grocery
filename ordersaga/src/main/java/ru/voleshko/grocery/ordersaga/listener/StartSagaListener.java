package ru.voleshko.grocery.ordersaga.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.ordersaga.dto.Order;
import ru.voleshko.grocery.ordersaga.saga.SagaProcessor;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StartSagaListener {

    private final SagaProcessor sagaProcessor;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "START_SAGA")
    @RabbitListener(queues = "startSagaQueue")
    public void startSaga(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order orderToProcess = objectMapper.readValue(order, Order.class);
        sagaProcessor.startSaga(orderToProcess);
    }
}
