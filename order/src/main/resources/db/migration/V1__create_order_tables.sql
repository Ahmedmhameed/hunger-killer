CREATE TABLE orders (
    id                         VARCHAR(36)    NOT NULL PRIMARY KEY,
    customer_id                VARCHAR(36)    NOT NULL,
    restaurant_id              VARCHAR(36)    NOT NULL,
    cart_id                    VARCHAR(36),
    delivery_address_id        VARCHAR(36),
    delivery_address           VARCHAR(500),
    payment_id                 VARCHAR(36),
    status                     VARCHAR(30)    NOT NULL,
    payment_method             VARCHAR(30)    NOT NULL,
    special_instructions       TEXT,
    subtotal                   DECIMAL(10,2)  NOT NULL,
    delivery_fee               DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    grand_total                DECIMAL(10,2)  NOT NULL,
    estimated_delivery_minutes INT,
    placed_at                  DATETIME,
    created_at                 DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted                    TINYINT(1)     NOT NULL DEFAULT 0
);

CREATE TABLE order_items (
    id          VARCHAR(36)   NOT NULL PRIMARY KEY,
    order_id    VARCHAR(36)   NOT NULL,
    item_id     VARCHAR(36),
    item_name   VARCHAR(255),
    unit_price  DECIMAL(10,2) NOT NULL,
    quantity    INT           NOT NULL,
    subtotal    DECIMAL(10,2) NOT NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT(1)    NOT NULL DEFAULT 0,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE order_status_history (
    id         VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id   VARCHAR(36) NOT NULL,
    status     VARCHAR(30) NOT NULL,
    changed_at DATETIME    NOT NULL,
    note       VARCHAR(255),
    CONSTRAINT fk_osh_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_orders_customer_id    ON orders(customer_id);
CREATE INDEX idx_orders_restaurant_id  ON orders(restaurant_id);
CREATE INDEX idx_orders_status         ON orders(status);
CREATE INDEX idx_order_items_order_id  ON order_items(order_id);
