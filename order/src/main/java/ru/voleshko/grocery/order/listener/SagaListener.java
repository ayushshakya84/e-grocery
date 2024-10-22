package ru.voleshko.grocery.order.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.order.OrderService;
import ru.voleshko.grocery.order.entity.Order;
import ru.voleshko.lib.idempotency.Idempotent;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
public class SagaListener {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final SagaListener self;

    public SagaListener(OutboxRepository outboxRepository,
                        ObjectMapper objectMapper,
                        OrderService orderService,
                        @Lazy SagaListener self
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.self = self;
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "CHANGE_ORDER_STATUS_SAGA")
    @RabbitListener(queues = "orderStatusRequestQueue")
    public void requestOrderStatusChange(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        UUID orderId = null;
        try {
            orderId = UUID.fromString(objectMapper.readTree(order).get("id").asText());
            orderService.changeOrderStatus(orderId, Order.OrderStatus.SUCCESS);
            outboxRepository.save(new OutboxMessage(
                    "orderExchange",
                    "orderStatusSuccessQueue",
                    order
            ));
        } catch (Exception exception) {
            log.error("Failed to change Order status for order with id = [{}]: ",
                    ofNullable(orderId).map(UUID::toString).orElse("<unknown>"), exception);
            self.handleException(order);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleException(String order) {
        outboxRepository.save(new OutboxMessage(
                "orderExchange",
                "orderStatusFailQueue",
                order
        ));
    }

}
