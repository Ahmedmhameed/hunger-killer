CREATE TABLE cart (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    restaurant_id VARCHAR(36),
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    expires_at DATETIME,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT uk_cart_customer_id UNIQUE (customer_id)
);

CREATE TABLE cart_item (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    cart_id VARCHAR(36) NOT NULL,
    item_id VARCHAR(36) NOT NULL,
    item_name VARCHAR(255),
    unit_price DECIMAL(10, 2),
    quantity INT NOT NULL CHECK (quantity > 0),
    subtotal DECIMAL(10, 2),
    special_notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart (id)
);

CREATE INDEX idx_cart_item_cart_id ON cart_item(cart_id);
