package com.ahmedkh.notification.dto.request;

import com.ahmedkh.notification.entity.NotificationType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    private String customerId;
    private String orderId;
    private String recipient;
    private String title;
    private String message;
    private NotificationType type;
}
