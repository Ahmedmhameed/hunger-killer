package com.ahmedkh.menu.kafka;

import com.ahmedkh.menu.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class InventoryEventConsumer {

    private final MenuItemService menuItemService;

    public InventoryEventConsumer(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            include = {Exception.class}
    )
    @KafkaListener(topics = "${spring.kafka.topics.inventory-stock:inventory.stock}",
                   groupId = "${spring.kafka.consumer.group-id:menu-service-group}")
    public void handleInventoryStockEvent(Map<String, Object> event) {
        try {
            log.info("Received inventory.stock event: {}", event);

            String itemId = (String) event.get("itemId");
            boolean isOutOfStock = (boolean) event.getOrDefault("isOutOfStock", false);

            if (itemId == null) {
                log.warn("Invalid inventory event: missing itemId");
                return;
            }

            // Update menu item availability based on stock status
            menuItemService.updateMenuItemAvailability(itemId, !isOutOfStock);
            log.info("Menu item availability updated for item: {}, available: {}", itemId, !isOutOfStock);

        } catch (Exception ex) {
            log.error("Error processing inventory.stock event: {}", event, ex);
            throw new RuntimeException("Failed to process inventory event", ex);
        }
    }
}
