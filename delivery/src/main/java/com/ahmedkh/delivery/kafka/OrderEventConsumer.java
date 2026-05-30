package com.ahmedkh.delivery.kafka;

import com.ahmedkh.delivery.dto.request.RequestDeliveryRequest;
import com.ahmedkh.delivery.service.DeliveryServiceInterface;
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

    private final DeliveryServiceInterface deliveryService;

    public OrderEventConsumer(DeliveryServiceInterface deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            include = {Exception.class}
    )
    @KafkaListener(topics = "${spring.kafka.topics.order-placed:order.placed}", 
                   groupId = "${spring.kafka.consumer.group-id:delivery-service-group}")
    public void handleOrderPlacedEvent(Map<String, Object> event) {
        try {
            log.info("Received order.placed event: {}", event);

            String orderId = (String) event.get("orderId");
            String customerId = (String) event.get("customerId");
            String deliveryAddress = (String) event.get("deliveryAddress");
            Double deliveryLatitude = ((Number) event.get("deliveryLatitude")).doubleValue();
            Double deliveryLongitude = ((Number) event.get("deliveryLongitude")).doubleValue();

            if (orderId == null || customerId == null || deliveryAddress == null) {
                log.warn("Invalid order event: missing required fields");
                return;
            }

            // Create delivery request from order event
            RequestDeliveryRequest deliveryRequest = RequestDeliveryRequest.builder()
                    .orderId(orderId)
                    .customerId(customerId)
                    .deliveryAddress(deliveryAddress)
                    .deliveryLatitude(deliveryLatitude)
                    .deliveryLongitude(deliveryLongitude)
                    .deliveryNotes((String) event.get("notes"))
                    .build();

            // Request delivery
            deliveryService.requestDelivery(deliveryRequest);
            log.info("Successfully created delivery for order: {}", orderId);

        } catch (Exception ex) {
            log.error("Error processing order.placed event: {}", event, ex);
            throw new RuntimeException("Failed to process order event", ex);
        }
    }
}
