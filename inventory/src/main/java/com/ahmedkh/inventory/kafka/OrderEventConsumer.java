package com.ahmedkh.inventory.kafka;

import com.ahmedkh.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    public OrderEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            include = {Exception.class}
    )
    @KafkaListener(topics = "${app.kafka.topics.order-placed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderPlacedEvent(Map<String, Object> event) {
        try {
            log.info("Received order.placed event: {}", event);
            inventoryService.handleOrderPlaced(event);
        } catch (Exception ex) {
            log.error("Error processing order.placed event: {}", event, ex);
            throw new RuntimeException("Failed to process order.placed event", ex);
        }
    }
}
