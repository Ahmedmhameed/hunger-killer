package com.ahmedkh.order.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes payment.confirmed events — analytics passthrough.
 * Order is already CONFIRMED synchronously during placement.
 */
@Component
public class PaymentConfirmedConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConfirmedConsumer.class);

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            dltTopicSuffix = ".DLT",
            include = {Exception.class}
    )
    @KafkaListener(topics = "${app.kafka.topics.payment-confirmed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentConfirmedEvent(Map<String, Object> event) {
        log.info("Received payment.confirmed event for orderId={}, status={}",
                event.get("orderId"), event.get("status"));
        // Order already confirmed synchronously at placement time.
        // This is a no-op for state; used for audit/analytics passthrough.
    }
}
