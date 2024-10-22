package ru.voleshko.lib.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessageSender {

    private final OutboxRepository outboxRepository;
    private final OutboxMessageProcessor outboxMessageProcessor;

    @Scheduled(cron = "0/1 * * * * ?")
    @SchedulerLock(name = "sendOutboxMessages", lockAtMostFor = "5m")
    public void sendOutboxMessages() {
        outboxRepository.findMessagesToSend().forEach(outboxMessageProcessor::processMessage);
    }
}
