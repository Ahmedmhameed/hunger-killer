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
 * Consumes kitchen.status events to advance order state machine:
 *   CONFIRMED → PREPARING  (when kitchen status = IN_PREPARATION)
 *   PREPARING → READY      (when kitchen status = READY)
 */
@Component
public class KitchenStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(KitchenStatusConsumer.class);
    private final OrderStateMachine orderStateMachine;

    public KitchenStatusConsumer(OrderStateMachine orderStateMachine) {
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
    @KafkaListener(topics = "${app.kafka.topics.kitchen-status}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleKitchenStatusEvent(Map<String, Object> event) {
        try {
            log.info("Received kitchen.status event: {}", event);
            String orderId = (String) event.get("orderId");
            String kitchenStatus = event.get("status") != null ? event.get("status").toString() : null;

            if (orderId == null || kitchenStatus == null) {
                log.warn("Invalid kitchen.status event: missing orderId or status");
                return;
            }

            orderStateMachine.applyKitchenStatusTransition(orderId, kitchenStatus);

        } catch (Exception ex) {
            log.error("Error processing kitchen.status event: {}", event, ex);
            throw new RuntimeException("Failed to process kitchen status event", ex);
        }
    }
}
