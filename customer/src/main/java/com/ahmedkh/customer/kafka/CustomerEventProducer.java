package com.ahmedkh.customer.kafka;

import com.ahmedkh.customer.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.customer-registered:customer.registered}")
    private String customerRegisteredTopic;

    public CustomerEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCustomerRegisteredEvent(Customer customer) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("customerId", customer.getId().toString());
            event.put("firstName", customer.getFirstName());
            event.put("lastName", customer.getLastName());
            event.put("email", customer.getEmail());
            event.put("phone", customer.getPhone());
            event.put("loyaltyPoints", customer.getLoyaltyPoints());
            event.put("createdAt", LocalDateTime.now());

            kafkaTemplate.send(customerRegisteredTopic, customer.getId().toString(), event);
            log.info("Published customer.registered event for customer: {}", customer.getId());
        } catch (Exception ex) {
            log.error("Error publishing customer.registered event for customer: {}", customer.getId(), ex);
        }
    }
}
