package com.hungerkiller.customer.kafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventProducer {

    private static final String TOPIC = "customer.registered";

    private final KafkaTemplate<String, CustomerRegisteredEvent> kafkaTemplate;

    public CustomerEventProducer(
            KafkaTemplate<String, CustomerRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes CustomerRegisteredEvent to Kafka topic "customer.registered".
     * Pattern: Fire-and-forget (event-driven).
     * Consumers: Notification MS (welcome email).
     * Customer MS does NOT know who subscribes — fully decoupled.
     */
    public void publishCustomerRegistered(CustomerRegisteredEvent event) {
        kafkaTemplate.send(TOPIC, event.getCustomerId(), event);
        System.out.println("[Kafka] Published CustomerRegisteredEvent for "
                + event.getEmail());
    }
}
