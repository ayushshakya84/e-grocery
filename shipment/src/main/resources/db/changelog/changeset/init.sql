CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE shipment (
    id uuid NOT NULL,
    status varchar NOT NULL,
    address text NOT NULL,
    order_id uuid NOT NULL,
    created_at timestamptz NOT NULL,
    shipment_from timestamptz NOT NULL,
    shipment_to timestamptz NOT NULL,
    CONSTRAINT shipment_pk PRIMARY KEY (id)
);
CREATE INDEX shipment_order_id_idx ON shipment (order_id);

CREATE TABLE shipment_item (
    id uuid NOT NULL,
    qty int NOT NULL,
    product_id uuid NOT NULL,
    product_name varchar(255) NOT NULL,
    shipment_id uuid NOT NULL,

    CONSTRAINT shipment_item_pk PRIMARY KEY (id),
    CONSTRAINT shipment_item_shipment_fk FOREIGN KEY (shipment_id) REFERENCES shipment (id)
);
CREATE INDEX reservation_item_shipment_idx ON shipment_item (shipment_id);

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
