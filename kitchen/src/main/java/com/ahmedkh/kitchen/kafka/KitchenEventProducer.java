package com.ahmedkh.kitchen.kafka;

import com.ahmedkh.kitchen.entity.KitchenTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KitchenEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.kitchen-status:kitchen.status}")
    private String kitchenStatusTopic;

    public KitchenEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishKitchenStatusEvent(KitchenTicket ticket) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("ticketId", ticket.getId().toString());
            event.put("orderId", ticket.getOrderId());
            event.put("status", ticket.getStatus().toString());
            event.put("estimatedReadyAt", ticket.getEstimatedReadyAt());
            event.put("actualReadyAt", ticket.getActualReadyAt());
            event.put("updatedAt", LocalDateTime.now());

            kafkaTemplate.send(kitchenStatusTopic, ticket.getId().toString(), event);
            log.info("Published kitchen.status event for ticket: {}", ticket.getId());
        } catch (Exception ex) {
            log.error("Error publishing kitchen.status event for ticket: {}", ticket.getId(), ex);
        }
    }
}
