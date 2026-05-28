CREATE TABLE IF NOT EXISTS kitchen_stations (
    id CHAR(36) NOT NULL PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    station_type VARCHAR(50) NOT NULL,
    current_load INT NOT NULL DEFAULT 0,
    max_capacity INT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_station_type (station_type),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
