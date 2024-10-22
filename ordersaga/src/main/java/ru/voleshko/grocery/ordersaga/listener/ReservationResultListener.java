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
import ru.voleshko.grocery.ordersaga.saga.step.ReservationStep;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationResultListener {

    private final SagaProcessor sagaProcessor;
    private final ObjectMapper objectMapper;
    private final ReservationStep reservationStep;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "RESERVATION_STATUS_SUCCESS")
    @RabbitListener(queues = "reservationSuccessQueue")
    public void reservationSuccess(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order successOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepSuccess(reservationStep, successOrder);
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "RESERVATION_STATUS_FAIL")
    @RabbitListener(queues = "reservationFailQueue")
    public void reservationError(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order errorOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepError(reservationStep, errorOrder);
    }
}
