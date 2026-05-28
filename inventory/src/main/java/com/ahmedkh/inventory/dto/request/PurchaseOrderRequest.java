package com.ahmedkh.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PurchaseOrderRequest {

    @NotNull(message = "Ingredient ID is required")
    private UUID ingredientId;

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    private String unit;
}
