package com.ahmedkh.inventory.kafka;

import com.hungerkiller.events.StockDepletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.inventory-stock}")
    private String inventoryStockTopic;

    public void publishStockDepletedEvent(StockDepletedEvent event) {
        log.info("Publishing StockDepletedEvent to topic {}: {}", inventoryStockTopic, event);
        kafkaTemplate.send(inventoryStockTopic, event.getItemId(), event);
    }
}
