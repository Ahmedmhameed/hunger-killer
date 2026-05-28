package com.ahmedkh.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Publishes order.placed events as plain JSON Maps.
 * Kitchen and Inventory services consume these as Map<String, Object>
 * using JsonDeserializer — this format is fully compatible with both consumers.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.order-placed}")
    private String orderPlacedTopic;

    public void publishOrderPlaced(String orderId, String customerId, String restaurantId,
                                   List<Map<String, Object>> items, double grandTotal,
                                   String deliveryAddress, String specialInstructions) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId);
        event.put("customerId", customerId);
        event.put("restaurantId", restaurantId);
        event.put("items", items);
        event.put("grandTotal", grandTotal);
        event.put("deliveryAddress", deliveryAddress != null ? deliveryAddress : "");
        event.put("specialInstructions", specialInstructions);
        event.put("placedAt", LocalDateTime.now().toString());

        log.info("Publishing order.placed event for orderId={}", orderId);
        kafkaTemplate.send(orderPlacedTopic, orderId, event);
    }
}
