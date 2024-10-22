CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE payment (
    id uuid NOT NULL,
    status varchar NOT NULL,
    amount numeric(7,2) NOT NULL,
    order_id uuid NOT NULL,
    card_number varchar(50) NOT NULL,
    card_valid_to varchar(50) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT payment_pk PRIMARY KEY (id)
);
CREATE INDEX payment_order_id_idx ON payment (order_id);
CREATE INDEX payment_created_at_idx ON payment (created_at);

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

CREATE TABLE outbox_message (
    idempotency_key uuid NOT NULL,
    payload text NOT NULL,
    exchange varchar(255) NOT NULL,
    queue varchar(255) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT outbox_message_pk PRIMARY KEY (idempotency_key)
);
