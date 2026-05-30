CREATE TABLE kitchen_tickets (
                                 id BINARY(16) NOT NULL PRIMARY KEY,
                                 order_id VARCHAR(36) NOT NULL UNIQUE,
                                 restaurant_id VARCHAR(36) NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 special_instructions TEXT,
                                 estimated_ready_at TIMESTAMP NULL,
                                 actual_ready_at TIMESTAMP NULL,
                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);