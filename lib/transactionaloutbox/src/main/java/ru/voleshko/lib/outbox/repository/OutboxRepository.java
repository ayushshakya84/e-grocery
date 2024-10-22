package ru.voleshko.lib.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.lib.outbox.entity.OutboxMessage;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query("SELECT m from OutboxMessage m ORDER BY m.createdAt ASC")
    List<OutboxMessage> findMessagesToSend();
}
