package com.ahmedkh.kitchen.kafka;

import com.ahmedkh.kitchen.service.KitchenTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderEventConsumer {

    private final KitchenTicketService ticketService;

    public OrderEventConsumer(KitchenTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            include = {Exception.class}
    )
    @KafkaListener(topics = "${spring.kafka.topics.order-placed:order.placed}", 
                   groupId = "${spring.kafka.consumer.group-id:kitchen-service-group}")
    public void handleOrderPlacedEvent(Map<String, Object> event) {
        try {
            log.info("Received order.placed event: {}", event);

            String orderId = (String) event.get("orderId");
            String restaurantId = (String) event.get("restaurantId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");

            if (orderId == null || restaurantId == null || items == null || items.isEmpty()) {
                log.warn("Invalid order event: missing required fields");
                return;
            }

            List<String> itemIds = items.stream()
                    .map(item -> (String) item.get("itemId"))
                    .toList();
            
            List<String> itemNames = items.stream()
                    .map(item -> (String) item.get("itemName"))
                    .toList();
            
            List<Integer> quantities = items.stream()
                    .map(item -> ((Number) item.get("quantity")).intValue())
                    .toList();

            ticketService.createTicketFromOrder(orderId, restaurantId, itemIds, itemNames, quantities);
            log.info("Successfully created kitchen ticket for order: {}", orderId);

        } catch (Exception ex) {
            log.error("Error processing order.placed event: {}", event, ex);
            throw new RuntimeException("Failed to process order event", ex);
        }
    }
}
