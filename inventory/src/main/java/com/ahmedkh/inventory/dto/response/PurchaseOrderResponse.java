package com.ahmedkh.inventory.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PurchaseOrderResponse {
    private UUID id;
    private String restaurantId;
    private UUID ingredientId;
    private String supplierName;
    private BigDecimal quantity;
    private String unit;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime deliveredAt;
}
