package ru.voleshko.lib.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessageProcessor {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void processMessage(OutboxMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    message.getExchange(), message.getQueue(), message.getPayload(),
                    m -> {
                        m.getMessageProperties().getHeaders().put("Idempotency-key", message.getIdempotencyKey());
                        return m;
                    }
            );
            outboxRepository.delete(message);
        } catch (Exception ignored) {}
    }

}
