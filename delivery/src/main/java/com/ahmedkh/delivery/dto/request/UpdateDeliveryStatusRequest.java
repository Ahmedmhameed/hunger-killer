package com.ahmedkh.delivery.dto.request;

import com.ahmedkh.delivery.entity.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeliveryStatusRequest {
    @NotNull(message = "Status is required")
    private DeliveryStatus status;

    private String notes;
}
