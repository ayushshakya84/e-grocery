package ru.voleshko.lib.outbox.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"idempotencyKey"})
public class OutboxMessage {

    @Id
    private UUID idempotencyKey = UUID.randomUUID();

    private String exchange;

    private String queue;

    private String payload;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    public OutboxMessage() {
    }

    public OutboxMessage(String exchange, String queue, String payload) {
        this.exchange = exchange;
        this.queue = queue;
        this.payload = payload;
    }
}
