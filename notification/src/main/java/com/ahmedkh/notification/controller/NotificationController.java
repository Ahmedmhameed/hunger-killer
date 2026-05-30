package com.ahmedkh.notification.controller;

import com.ahmedkh.notification.dto.request.SendNotificationRequest;
import com.ahmedkh.notification.dto.response.ApiResponse;
import com.ahmedkh.notification.dto.response.NotificationResponse;
import com.ahmedkh.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management and dispatch API (Mock Implementation)")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Health check / Hello endpoint
     */
    @GetMapping("/")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification Service is running", "v1.0.0"));
    }

    /**
     * Mock send a notification
     */
    @PostMapping("/send")
    @Operation(summary = "Send a mock notification manually")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @RequestBody SendNotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Notification sent and logged successfully", response));
    }

    /**
     * Get all active notifications
     */
    @GetMapping
    @Operation(summary = "Get all active (non-deleted) notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        List<NotificationResponse> response = notificationService.getAllNotifications();
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved", response));
    }

    /**
     * Get notifications for a customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all notifications for a specific customer")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByCustomer(
            @PathVariable String customerId) {
        List<NotificationResponse> response = notificationService.getNotificationsByCustomer(customerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer notifications retrieved", response));
    }

    /**
     * Get notifications for an order
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get all notifications for a specific order")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByOrder(
            @PathVariable String orderId) {
        List<NotificationResponse> response = notificationService.getNotificationsByOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order notifications retrieved", response));
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get notification details by ID")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(@PathVariable UUID id) {
        NotificationResponse response = notificationService.getNotification(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification retrieved", response));
    }

    /**
     * Mark notification as read
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read", response));
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification deleted successfully", null));
    }
}
