package com.ahmedkh.notification.service;

import com.ahmedkh.notification.dto.request.SendNotificationRequest;
import com.ahmedkh.notification.dto.response.NotificationResponse;
import com.ahmedkh.notification.entity.Notification;
import com.ahmedkh.notification.entity.NotificationStatus;
import com.ahmedkh.notification.entity.NotificationType;
import com.ahmedkh.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Mock sends a notification (email, sms, push, or in-app), stores it in database, and returns the details.
     */
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending notification: title='{}', recipient='{}', type={}",
                request.getTitle(), request.getRecipient(), request.getType());

        NotificationType type = request.getType() != null ? request.getType() : NotificationType.IN_APP;

        Notification notification = Notification.builder()
                .customerId(request.getCustomerId())
                .orderId(request.getOrderId())
                .recipient(request.getRecipient())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(type)
                .status(NotificationStatus.SENT) // Mocking immediate successful transmission
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        // Safe defaults for BaseEntity values that will be populated by JPA Auditing
        notification.setDeleted(false);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification stored in database with ID: {}", savedNotification.getId());

        return mapToResponse(savedNotification);
    }

    /**
     * Get all active (non-deleted) notifications
     */
    public List<NotificationResponse> getAllNotifications() {
        log.info("Fetching all active notifications");
        return notificationRepository.findByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active notifications for a specific customer
     */
    public List<NotificationResponse> getNotificationsByCustomer(String customerId) {
        log.info("Fetching notifications for customer: {}", customerId);
        return notificationRepository.findByCustomerIdAndDeletedFalseOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active notifications for a specific order
     */
    public List<NotificationResponse> getNotificationsByOrder(String orderId) {
        log.info("Fetching notifications for order: {}", orderId);
        return notificationRepository.findByOrderIdAndDeletedFalseOrderByCreatedAtDesc(orderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific active notification by ID
     */
    public NotificationResponse getNotification(UUID id) {
        log.info("Fetching notification with ID: {}", id);
        Notification notification = notificationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Active notification not found with ID: " + id));
        return mapToResponse(notification);
    }

    /**
     * Mark a notification as read
     */
    public NotificationResponse markAsRead(UUID id) {
        log.info("Marking notification {} as read", id);
        Notification notification = notificationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Active notification not found with ID: " + id));

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return mapToResponse(updatedNotification);
    }

    /**
     * Soft delete a notification
     */
    public void deleteNotification(UUID id) {
        log.info("Soft deleting notification with ID: {}", id);
        Notification notification = notificationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Active notification not found with ID: " + id));

        notification.setDeleted(true);
        notificationRepository.save(notification);
        log.info("Notification {} soft deleted successfully", id);
    }

    /**
     * Map Notification entity to NotificationResponse DTO
     */
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .customerId(notification.getCustomerId())
                .orderId(notification.getOrderId())
                .recipient(notification.getRecipient())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt() != null ? notification.getCreatedAt() : notification.getSentAt())
                .sentAt(notification.getSentAt())
                .isRead(notification.getIsRead())
                .build();
    }
}
