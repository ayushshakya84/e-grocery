CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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

CREATE TABLE notification_template (
    notification_type VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    template VARCHAR(1000) NOT NULL,
    PRIMARY KEY (notification_type)
);
