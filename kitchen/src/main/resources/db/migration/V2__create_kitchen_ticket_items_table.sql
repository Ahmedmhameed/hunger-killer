CREATE TABLE kitchen_ticket_items (
                                      id BINARY(16) NOT NULL PRIMARY KEY,
                                      ticket_id BINARY(16) NOT NULL,
                                      item_id VARCHAR(36) NOT NULL,
                                      item_name VARCHAR(255) NOT NULL,
                                      quantity INT NOT NULL,
                                      station VARCHAR(50) NOT NULL,
                                      deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_ticket
                                          FOREIGN KEY (ticket_id)
                                              REFERENCES kitchen_tickets(id)
);