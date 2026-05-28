package com.ahmedkh.order.kafka;

import com.ahmedkh.order.statemachine.OrderStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes delivery.status events to advance order state machine:
 *   READY       → DELIVERING  (when delivery status = ASSIGNED or PICKED_UP)
 *   DELIVERING  → DELIVERED   (when delivery status = DELIVERED)
 */
@Component
public class DeliveryStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryStatusConsumer.class);
    private final OrderStateMachine orderStateMachine;

    public DeliveryStatusConsumer(OrderStateMachine orderStateMachine) {
        this.orderStateMachine = orderStateMachine;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            dltTopicSuffix = ".DLT",
            include = {Exception.class}
    )
    @KafkaListener(topics = "${app.kafka.topics.delivery-status}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDeliveryStatusEvent(Map<String, Object> event) {
        try {
            log.info("Received delivery.status event: {}", event);
            String orderId = (String) event.get("orderId");
            String deliveryStatus = event.get("status") != null ? event.get("status").toString() : null;

            if (orderId == null || deliveryStatus == null) {
                log.warn("Invalid delivery.status event: missing orderId or status");
                return;
            }

            orderStateMachine.applyDeliveryStatusTransition(orderId, deliveryStatus);

        } catch (Exception ex) {
            log.error("Error processing delivery.status event: {}", event, ex);
            throw new RuntimeException("Failed to process delivery status event", ex);
        }
    }
}
