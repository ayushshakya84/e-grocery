package ru.voleshko.grocery.ordersaga.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.voleshko.grocery.ordersaga.repository.AuditLogRepository;
import ru.voleshko.grocery.ordersaga.saga.step.OrderStatusStep;
import ru.voleshko.grocery.ordersaga.saga.step.PaymentStep;
import ru.voleshko.grocery.ordersaga.saga.step.ReservationStep;
import ru.voleshko.grocery.ordersaga.saga.step.ShipmentStep;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

@Configuration
public class SagaProcessorConfig {

    @Bean
    public SagaProcessor sagaProcessor(AuditLogRepository auditLogRepository,
                                       OutboxRepository outboxRepository,
                                       ObjectMapper objectMapper
    ) {
        return new SagaProcessor.builder()
                .auditLogRepository(auditLogRepository)
                .outboxRepository(outboxRepository)
                .withStep(new PaymentStep(outboxRepository, objectMapper))
                .withStep(new ShipmentStep(outboxRepository, objectMapper))
                .withStep(new ReservationStep(outboxRepository, objectMapper))
                .withStep(new OrderStatusStep(outboxRepository, objectMapper))
                .build();
    }

}
