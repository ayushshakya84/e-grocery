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
import ru.voleshko.grocery.ordersaga.saga.step.ShipmentStep;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShipmentResultListener {

    private final SagaProcessor sagaProcessor;
    private final ObjectMapper objectMapper;
    private final ShipmentStep shipmentStep;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "SHIPMENT_STATUS_SUCCESS")
    @RabbitListener(queues = "shipmentSuccessQueue")
    public void shipmentSuccess(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order successOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepSuccess(shipmentStep, successOrder);
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "SHIPMENT_STATUS_FAIL")
    @RabbitListener(queues = "shipmentFailQueue")
    public void shipmentError(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        Order errorOrder = objectMapper.readValue(order, Order.class);
        sagaProcessor.handleStepError(shipmentStep, errorOrder);
    }
}
