CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE audit_log_record (
    id uuid NOT NULL,
    order_id uuid NOT NULL,
    event varchar(255) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT audit_log_record_pk PRIMARY KEY (id)
);
CREATE INDEX audit_log_record_order_id_idx ON audit_log_record (order_id);
CREATE INDEX audit_log_record_created_at_idx ON audit_log_record (created_at);

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
