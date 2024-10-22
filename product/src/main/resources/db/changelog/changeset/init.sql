CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE category (
    id uuid NOT NULL,
    "name" varchar NOT NULL,
    description text NULL,
    is_leaf boolean NOT NULL DEFAULT false,
    parent_category_id uuid NULL,
    CONSTRAINT category_pk PRIMARY KEY (id),
    CONSTRAINT category_fk FOREIGN KEY (parent_category_id) REFERENCES category(id)
);
CREATE UNIQUE INDEX category_name_idx ON category ("name");
CREATE INDEX category_parent_category_id_idx ON category (parent_category_id);

CREATE TABLE "attribute" (
    id uuid NOT NULL,
    "name" varchar NOT NULL,
    "type" varchar NOT NULL,
    CONSTRAINT attribute_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX attribute_name_idx ON "attribute" ("name");
COMMENT ON TABLE "attribute" IS 'Table to store dynamic attributes of categories';

CREATE TABLE category_attribute (
    category_id uuid NOT NULL,
    attribute_id uuid NOT NULL,
    CONSTRAINT category_attribute_pk PRIMARY KEY (category_id,attribute_id),
    CONSTRAINT category_attribute_category_fk FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT category_attribute_attribute_fk FOREIGN KEY (attribute_id) REFERENCES "attribute"(id)
);

CREATE TABLE product (
    id uuid NOT NULL,
    "name" varchar NOT NULL,
    description text NULL,
    weight float4 NULL,
    price numeric(7,2) NOT NULL,
    qty int NOT NULL,
    discount_percent smallint NULL,
    "attributes" text NULL,
    CONSTRAINT product_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX product_name_idx ON product ("name");

CREATE TABLE category_product (
    category_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT category_product_pk PRIMARY KEY (category_id,product_id),
    CONSTRAINT category_product_category_fk FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT category_product_product_fk FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE price (
    id uuid NOT NULL,
    start_date timestamptz NOT NULL,
    value numeric(7,2) NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT price_pk PRIMARY KEY (id),
    CONSTRAINT price_fk FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
CREATE INDEX price_start_date_idx ON price (start_date);

CREATE TABLE reservation (
    id uuid NOT NULL,
    status varchar NOT NULL,
    order_id uuid NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT reservation_pk PRIMARY KEY (id)
);
CREATE INDEX reservation_order_id_idx ON reservation (order_id);

CREATE TABLE reservation_item (
    id uuid NOT NULL,
    qty int NOT NULL,
    product_id uuid NOT NULL,
    reservation_id uuid NOT NULL,

    CONSTRAINT reservation_item_pk PRIMARY KEY (id),
	CONSTRAINT reservation_item_reservation_fk FOREIGN KEY (reservation_id) REFERENCES reservation(id),
	CONSTRAINT reservation_item_product_fk FOREIGN KEY (product_id) REFERENCES product(id)
);
CREATE INDEX reservation_item_order_id_idx ON reservation_item (reservation_id);

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
