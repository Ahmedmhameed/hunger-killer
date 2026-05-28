CREATE TABLE category (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    display_order INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE menu_item (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(500),
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    preparation_time_minutes INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_menu_item_category FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE INDEX idx_menu_item_restaurant_id ON menu_item(restaurant_id);
CREATE INDEX idx_category_restaurant_id ON category(restaurant_id);
