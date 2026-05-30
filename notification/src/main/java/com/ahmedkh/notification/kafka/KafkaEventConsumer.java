package com.ahmedkh.notification.kafka;

import com.ahmedkh.notification.dto.request.SendNotificationRequest;
import com.ahmedkh.notification.entity.NotificationType;
import com.ahmedkh.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final NotificationService notificationService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            dltTopicSuffix = ".DLT",
            include = {Exception.class}
    )
    @KafkaListener(topics = "${app.kafka.topics.customer-registered}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleCustomerRegisteredEvent(Map<String, Object> event) {
        try {
            log.info("Received customer.registered event: {}", event);
            String customerId = (String) event.get("customerId");
            String firstName = (String) event.get("firstName");
            String lastName = (String) event.get("lastName");
            String email = (String) event.get("email");

            if (customerId == null) {
                log.warn("Invalid customer.registered event: missing customerId");
                return;
            }

            String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
            String message = String.format("Hello %s! Welcome to Hunger Killer. Your registration was successful.", fullName.trim());

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .customerId(customerId)
                    .recipient(email != null ? email : "customer_" + customerId + "@example.com")
                    .title("Welcome to Hunger Killer! 🍔")
                    .message(message)
                    .type(NotificationType.EMAIL)
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception ex) {
            log.error("Error processing customer.registered event: {}", event, ex);
            throw new RuntimeException("Failed to process customer registration event", ex);
        }
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000),
            autoCreateTopic = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_EXCEPTION_CLASS_NAME,
            dltTopicSuffix = ".DLT",
            include = {Exception.class}
    )
    @KafkaListener(topics = "${app.kafka.topics.order-placed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderPlacedEvent(Map<String, Object> event) {
        try {
            log.info("Received order.placed event: {}", event);
            String orderId = (String) event.get("orderId");
            String customerId = (String) event.get("customerId");
            Object grandTotalObj = event.get("grandTotal");

            if (orderId == null || customerId == null) {
                log.warn("Invalid order.placed event: missing orderId or customerId");
                return;
            }

            double grandTotal = 0.0;
            if (grandTotalObj instanceof Number) {
                grandTotal = ((Number) grandTotalObj).doubleValue();
            }

            String message = String.format("Your order #%s has been successfully placed. Total amount: $%.2f.", orderId, grandTotal);

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .customerId(customerId)
                    .orderId(orderId)
                    .recipient("customer_" + customerId + "@example.com")
                    .title("Order Placed Successfully 🛒")
                    .message(message)
                    .type(NotificationType.PUSH)
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception ex) {
            log.error("Error processing order.placed event: {}", event, ex);
            throw new RuntimeException("Failed to process order placed event", ex);
        }
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
            String status = event.get("status") != null ? event.get("status").toString() : null;

            if (orderId == null || status == null) {
                log.warn("Invalid kitchen.status event: missing orderId or status");
                return;
            }

            String message = String.format("Kitchen update: Your order #%s is now %s.", orderId, status.toLowerCase().replace('_', ' '));

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .orderId(orderId)
                    .recipient("order_" + orderId + "@customer.com")
                    .title("Kitchen Status Update 🍳")
                    .message(message)
                    .type(NotificationType.IN_APP)
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception ex) {
            log.error("Error processing kitchen.status event: {}", event, ex);
            throw new RuntimeException("Failed to process kitchen status event", ex);
        }
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
            String customerId = (String) event.get("customerId");
            String status = event.get("status") != null ? event.get("status").toString() : null;

            if (orderId == null || status == null) {
                log.warn("Invalid delivery.status event: missing orderId or status");
                return;
            }

            String message;
            String title = "Delivery Status Update 🛵";

            if ("ASSIGNED".equalsIgnoreCase(status)) {
                String driverName = (String) event.get("driverName");
                String vehicleNumber = (String) event.get("vehicleNumber");
                message = String.format("Driver %s (Vehicle: %s) has been assigned to your order #%s.",
                        driverName != null ? driverName : "Partner",
                        vehicleNumber != null ? vehicleNumber : "N/A",
                        orderId);
                title = "Driver Assigned! 🛵";
            } else if ("IN_TRANSIT".equalsIgnoreCase(status)) {
                message = String.format("Your order #%s is now in transit and on its way to you!", orderId);
                title = "Order in Transit! 🚀";
            } else if ("DELIVERED".equalsIgnoreCase(status)) {
                message = String.format("Your order #%s has been successfully delivered. Enjoy your meal!", orderId);
                title = "Order Delivered! 🎉";
            } else {
                message = String.format("Delivery update: Your order #%s status is now %s.", orderId, status.toLowerCase().replace('_', ' '));
            }

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .customerId(customerId)
                    .orderId(orderId)
                    .recipient("customer_" + (customerId != null ? customerId : "order_" + orderId) + "@example.com")
                    .title(title)
                    .message(message)
                    .type(NotificationType.SMS)
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception ex) {
            log.error("Error processing delivery.status event: {}", event, ex);
            throw new RuntimeException("Failed to process delivery status event", ex);
        }
    }

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
        try {
            log.info("Received payment.confirmed event: {}", event);
            String orderId = (String) event.get("orderId");
            String customerId = (String) event.get("customerId");
            String status = event.get("status") != null ? event.get("status").toString() : null;

            if (orderId == null) {
                log.warn("Invalid payment.confirmed event: missing orderId");
                return;
            }

            String message = String.format("Payment for order #%s has been confirmed. Status: %s.", orderId, status != null ? status : "APPROVED");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .customerId(customerId)
                    .orderId(orderId)
                    .recipient("customer_" + (customerId != null ? customerId : "order_" + orderId) + "@example.com")
                    .title("Payment Confirmed 💳")
                    .message(message)
                    .type(NotificationType.EMAIL)
                    .build();

            notificationService.sendNotification(request);

        } catch (Exception ex) {
            log.error("Error processing payment.confirmed event: {}", event, ex);
            throw new RuntimeException("Failed to process payment confirmed event", ex);
        }
    }
}
