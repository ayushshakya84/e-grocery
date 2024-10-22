package ru.voleshko.lib.idempotency;


import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class CleanUpIdempotencyKeysJob {

    private final EntityManager entityManager;

    @Transactional
    @Scheduled(fixedRate = 60*60*1000)
    @SchedulerLock(name = "cleanUpIdempotencyKeys", lockAtMostFor = "30m")
    public void cleanUp() {
        entityManager.createNativeQuery("DELETE FROM idempotency_key WHERE created < :date")
                .setParameter("date", ZonedDateTime.now().minusHours(24))
                .executeUpdate();
    }
}
