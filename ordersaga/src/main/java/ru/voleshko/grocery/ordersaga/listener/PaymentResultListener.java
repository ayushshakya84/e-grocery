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
import ru.voleshko.grocery.ordersaga.saga.step.PaymentStep;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final SagaProcessor sagaProcessor;
    private final ObjectMapper objectMapper;
    private final PaymentStep paymentStep;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "PAYMENT_STATUS_SUCCESS")
    @RabbitListener(queues = "paymentSuccessQueue")
    public void paymentSuccess(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order successOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepSuccess(paymentStep, successOrder);
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "PAYMENT_STATUS_FAIL")
    @RabbitListener(queues = "paymentFailQueue")
    public void paymentError(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order errorOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepError(paymentStep, errorOrder);
    }
}
