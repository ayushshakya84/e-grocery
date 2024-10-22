###### Introduction

Small Spring-based library to provide idempotency support.

If idempotency key is provided inside a request (RPC or via message broker),
first the check is made, if this key was already received before. 
If yes, no business-logic would be executed.
If no, provided idempotency key would be saved to the database and business-logic would be executed.

###### Quick start:
- Add library dependency
- Create tables:

```
CREATE TABLE idempotency_key (
    key uuid not null,
    operation varchar(255),
    created timestamptz,
    CONSTRAINT idempotency_key_pk PRIMARY KEY (key, operation)
);

CREATE TABLE shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

```
- Annotate method that should be idempotent with `@Idempotent(operation=...)`. Operation argument
is a discriminator value of operation and must be unique inside an application.
(the uniqueness of idempotency key would be checked 
using it). Also method should be annotated with `@Transactional`

- Among method arguments explicitly should be defined `UUID idempotencyKey` key

- Stored idempotency keys are cleaned up after 24 hours by scheduled job

###### Example of usage

```
    @Transactional
    @Idempotent(operation = "PAYMENT")
    @RabbitListener(queues = "paymentRequestQueue")
    public void requestPayment(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        ... business-logic goes here ...
    }
```
