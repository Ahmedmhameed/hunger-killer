package com.ahmedkh.delivery.dto.response;

import com.ahmedkh.delivery.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {

    private UUID id;

    private String orderId;

    private String customerId;

    private String deliveryAddress;

    private DeliveryStatus status;

    private String driverId;

    private String driverName;

    private String driverPhone;

    private String vehicleNumber;

    private Double currentLatitude;

    private Double currentLongitude;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime actualDeliveryTime;

    private String deliveryNotes;

    private String customerFeedback;

    private Integer rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
