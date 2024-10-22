CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "order"
(
    id uuid not null,
    total_price numeric(6, 2),
    address text not null,
    shipment_from timestamptz not null,
    shipment_to timestamptz not null,
    status varchar(50) not null,
    created_at timestamptz not null,
    user_id uuid not null,
    user_name varchar(50) not null,
    user_email varchar(50) not null
);

CREATE UNIQUE INDEX order_id_uindex
    on "order" (id);
CREATE INDEX user_id_idx ON "order" (user_id);

ALTER TABLE "order" ADD CONSTRAINT order_pk PRIMARY KEY (id);

CREATE TABLE order_item (
    id uuid NOT NULL,
    qty int4 NOT NULL,
    product_id uuid NOT NULL,
    order_id uuid NOT NULL,
    product_name varchar NOT NULL,
    product_price numeric(7,3) NOT NULL,
    CONSTRAINT order_item_pk PRIMARY KEY (id)
);
CREATE INDEX order_item_order_id_idx ON order_item USING btree (order_id);

ALTER TABLE order_item ADD CONSTRAINT order_item_fk_1 FOREIGN KEY (order_id) REFERENCES "order"(id);

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
