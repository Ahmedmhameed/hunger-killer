CREATE TABLE IF NOT EXISTS kitchen_tickets (
    id CHAR(36) NOT NULL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    restaurant_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) NOT NULL,
    special_instructions TEXT,
    estimated_ready_at TIMESTAMP,
    actual_ready_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
