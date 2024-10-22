###### Introduction

Small Spring-based library to provide transactional outbox pattern support.

Instead of direct calling `RabbitTemplate#convertAndSend` use `OutboxRepository#save`

OutboxMessage will be persisted and sent by built-in scheduler to the message broker.

###### Quick start:
- Add library dependency
- Create tables:

```
CREATE TABLE outbox_message (
    idempotency_key uuid NOT NULL,
    payload text NOT NULL,
    exchange varchar(255) NOT NULL,
    queue varchar(255) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT outbox_message_pk PRIMARY KEY (idempotency_key)
);

CREATE TABLE shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

```
- To send message to message broker it is enough to call `OutboxRepository#save`.
After sending, outbox messages will be deleted out of the database table.

(*) Scheduler actions are synchronized between multiple instances of application 
