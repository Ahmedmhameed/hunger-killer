CREATE TABLE ingredient_stock (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    item_id VARCHAR(36) NOT NULL,
    item_name VARCHAR(255),
    restaurant_id VARCHAR(36) NOT NULL,
    ingredient_name VARCHAR(255),
    quantity_available DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50),
    reorder_level DECIMAL(10, 2),
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    last_updated DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE purchase_order (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    restaurant_id VARCHAR(36),
    ingredient_id VARCHAR(36),
    supplier_name VARCHAR(255),
    quantity DECIMAL(10, 2),
    unit VARCHAR(50),
    status VARCHAR(50),
    requested_at DATETIME,
    delivered_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_po_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredient_stock(id)
);

CREATE INDEX idx_ingredient_item_id ON ingredient_stock(item_id);
CREATE INDEX idx_ingredient_restaurant_id ON ingredient_stock(restaurant_id);
