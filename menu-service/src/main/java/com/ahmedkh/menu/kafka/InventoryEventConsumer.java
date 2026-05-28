package com.ahmedkh.menu.kafka;

import com.ahmedkh.menu.entity.MenuItem;
import com.ahmedkh.menu.repository.MenuItemRepository;
import com.hungerkiller.events.StockDepletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final MenuItemRepository menuItemRepository;

    @KafkaListener(topics = "${app.kafka.topics.inventory-stock}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consumeStockDepletedEvent(StockDepletedEvent event) {
        log.info("Received StockDepletedEvent for itemId: {}, isOutOfStock: {}", event.getItemId(), event.getIsOutOfStock());

        if (Boolean.TRUE.equals(event.getIsOutOfStock())) {
            try {
                UUID itemId = UUID.fromString(event.getItemId());
                menuItemRepository.findById(itemId).ifPresent(menuItem -> {
                    menuItem.setIsAvailable(false);
                    menuItemRepository.save(menuItem);
                    log.info("Menu item {} set to unavailable due to stock depletion", itemId);
                });
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format for itemId: {}", event.getItemId(), e);
            }
        }
    }
}
