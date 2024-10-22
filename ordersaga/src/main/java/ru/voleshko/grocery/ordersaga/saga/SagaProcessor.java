package ru.voleshko.grocery.ordersaga.saga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import ru.voleshko.grocery.ordersaga.dto.Order;
import ru.voleshko.grocery.ordersaga.entity.AuditLogRecord;
import ru.voleshko.grocery.ordersaga.repository.AuditLogRepository;
import ru.voleshko.grocery.ordersaga.saga.step.SagaStep;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SagaProcessor {

    private final List<SagaStep> sagaSteps;
    private final List<String> sagaStepNames;

    private final AuditLogRepository auditLogRepository;
    private final OutboxRepository outboxRepository;

    public SagaProcessor(AuditLogRepository auditLogRepository,
                         OutboxRepository outboxRepository,
                         List<SagaStep> sagaSteps) {
        this.auditLogRepository = auditLogRepository;
        this.outboxRepository = outboxRepository;
        this.sagaSteps = sagaSteps;
        this.sagaStepNames = sagaSteps.stream()
                .map(this::className)
                .collect(Collectors.toList());
    }

    public static class builder {

        private static AuditLogRepository auditLogRepo;
        private static OutboxRepository outbox;
        private static final List<SagaStep> sagaSteps = new ArrayList<>();

        public builder auditLogRepository(AuditLogRepository auditLogRepository) {
            auditLogRepo = auditLogRepository;
            return this;
        }

        public builder outboxRepository(OutboxRepository outboxRepository) {
            outbox = outboxRepository;
            return this;
        }

        public builder withStep(SagaStep step) {
            sagaSteps.add(step);
            return this;
        }

        public SagaProcessor build() {
            return new SagaProcessor(auditLogRepo, outbox, sagaSteps);
        }
    }

    public void startSaga(Order order) {
        sagaSteps.get(0).doStep(order);
    }

    @Transactional
    public void handleStepSuccess(SagaStep step, Order order) {
        int currentStepIdx = getCurrentStepIdx(step);
        if (currentStepIdx < sagaSteps.size() - 1) {
            sagaSteps.get(currentStepIdx + 1).doStep(order);
        }

        saveAuditLog(order, step, "SUCCEED");
    }

    @Transactional
    public void handleStepError(SagaStep step, Order order) {
        saveAuditLog(order, step, "FAILED AND ROLLBACK");

        int currentStepIdx = getCurrentStepIdx(step);

        for (int i = currentStepIdx - 1; i >= 0; i--) {
            sagaSteps.get(i).handleError(order);
            saveAuditLog(order, sagaSteps.get(i), "ROLLBACK");
        }
        outboxRepository.save(new OutboxMessage(
                "orderExchange",
                "changeOrderStatusQueue",
                new ChangeOrderStatusDto(order.getId(), "ERROR").toString())
        );
    }

    private AuditLogRecord saveAuditLog(Order order, SagaStep sagaStep, String eventType) {
        return auditLogRepository.save(
                new AuditLogRecord(
                        null,
                        order.getId(),
                        className(sagaStep)
                                .replace("Step", " " + eventType)
                                .toUpperCase(),
                        ZonedDateTime.now()
                )
        );
    }

    private int getCurrentStepIdx(SagaStep sagaStep) {
        return sagaStepNames.indexOf(className(sagaStep));
    }

    private String className(Object obj) {
        return ClassUtils.getUserClass(obj.getClass()).getSimpleName();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ChangeOrderStatusDto {

        private UUID orderId;
        private String status;

        @Override
        public String toString() {
            return "{" +
                        "\"orderId\": \"" + orderId + "\"" +
                        ", \"status\": \"" + status + "\"" +
                    '}';
        }
    }

}
