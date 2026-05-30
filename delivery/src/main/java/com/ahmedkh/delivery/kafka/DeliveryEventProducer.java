package com.ahmedkh.delivery.kafka;

import com.ahmedkh.delivery.entity.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DeliveryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.delivery-status:delivery.status}")
    private String deliveryStatusTopic;

    public DeliveryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDeliveryStatusEvent(Delivery delivery) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("deliveryId", delivery.getId().toString());
            event.put("orderId", delivery.getOrderId());
            event.put("customerId", delivery.getCustomerId());
            event.put("status", delivery.getStatus().toString());
            event.put("driverId", delivery.getDriverId());
            event.put("driverName", delivery.getDriverName());
            event.put("driverPhone", delivery.getDriverPhone());
            event.put("vehicleNumber", delivery.getVehicleNumber());
            event.put("currentLatitude", delivery.getCurrentLatitude());
            event.put("currentLongitude", delivery.getCurrentLongitude());
            event.put("deliveryLatitude", delivery.getDeliveryLatitude());
            event.put("deliveryLongitude", delivery.getDeliveryLongitude());
            event.put("estimatedDeliveryTime", delivery.getEstimatedDeliveryTime());
            event.put("actualDeliveryTime", delivery.getActualDeliveryTime());
            event.put("updatedAt", LocalDateTime.now());

            kafkaTemplate.send(deliveryStatusTopic, delivery.getId().toString(), event);
            log.info("Published delivery.status event for delivery: {}", delivery.getId());
        } catch (Exception ex) {
            log.error("Error publishing delivery.status event for delivery: {}", delivery.getId(), ex);
        }
    }
}
