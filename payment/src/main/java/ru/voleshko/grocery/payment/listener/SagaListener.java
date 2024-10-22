package ru.voleshko.grocery.payment.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.payment.PaymentService;
import ru.voleshko.grocery.payment.dto.PaymentDto;
import ru.voleshko.lib.idempotency.Idempotent;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.UUID;

@Slf4j
@Component
public class SagaListener {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final SagaListener self;

    public SagaListener(OutboxRepository outboxRepository,
                        ObjectMapper objectMapper,
                        PaymentService paymentService,
                        @Lazy SagaListener self
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.self = self;
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "PAYMENT")
    @RabbitListener(queues = "paymentRequestQueue")
    public void requestPayment(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        PaymentDto dto = objectMapper.readValue(order, PaymentDto.class);
        log.info("Doing payment for order with id=[{}]", dto.getOrderId());
        try {
            paymentService.doOrderPayment(
                    dto.getOrderId(),
                    dto.getTotalPrice(),
                    dto.getCardNumber(),
                    dto.getCardValidTo(),
                    dto.getCardCvv()
            );
            outboxRepository.save(new OutboxMessage(
                    "paymentExchange",
                    "paymentSuccessQueue",
                    order)
            );
        } catch (Exception exception) {
            log.error("Failed to make payment for order with id = [{}]: ", dto.getOrderId(), exception);
            self.handleException(order);
        }
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "REFUND")
    @RabbitListener(queues = "paymentRollbackQueue")
    public void rollbackPayment(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        JsonNode parsedOrder = objectMapper.readTree(order);
        UUID orderId = UUID.fromString(parsedOrder.get("id").asText());

        paymentService.refund(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleException(String order) {
        outboxRepository.save(new OutboxMessage(
                "paymentExchange",
                "paymentFailQueue",
                order
        ));
    }
}
