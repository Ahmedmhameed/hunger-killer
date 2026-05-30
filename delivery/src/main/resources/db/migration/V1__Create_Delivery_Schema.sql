CREATE TABLE IF NOT EXISTS deliveries (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    driver_id VARCHAR(36),
    driver_name VARCHAR(100),
    driver_phone VARCHAR(20),
    vehicle_number VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    current_latitude DECIMAL(10,8),
    current_longitude DECIMAL(11,8),
    delivery_latitude DECIMAL(10,8),
    delivery_longitude DECIMAL(11,8),
    estimated_delivery_time TIMESTAMP,
    actual_delivery_time TIMESTAMP,
    delivery_notes TEXT,
    customer_feedback TEXT,
    rating INT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE KEY uk_order_id (order_id)
);

CREATE INDEX idx_customer_id ON deliveries(customer_id);
CREATE INDEX idx_status ON deliveries(status);
CREATE INDEX idx_driver_id ON deliveries(driver_id);
CREATE INDEX idx_created_at ON deliveries(created_at);
