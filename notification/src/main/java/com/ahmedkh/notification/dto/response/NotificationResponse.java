package com.ahmedkh.notification.dto.response;

import com.ahmedkh.notification.entity.NotificationStatus;
import com.ahmedkh.notification.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private String customerId;
    private String orderId;
    private String recipient;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private boolean isRead;
}
