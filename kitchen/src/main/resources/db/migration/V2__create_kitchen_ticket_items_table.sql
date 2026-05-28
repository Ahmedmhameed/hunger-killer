CREATE TABLE IF NOT EXISTS kitchen_ticket_items (
    id CHAR(36) NOT NULL PRIMARY KEY,
    ticket_id CHAR(36) NOT NULL,
    item_id VARCHAR(36) NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    station VARCHAR(50) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES kitchen_tickets(id),
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_station (station),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
