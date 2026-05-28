package com.ahmedkh.inventory.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class IngredientStockResponse {
    private UUID id;
    private String itemId;
    private String itemName;
    private String restaurantId;
    private String ingredientName;
    private BigDecimal quantityAvailable;
    private String unit;
    private BigDecimal reorderLevel;
    private Boolean isAvailable;
    private LocalDateTime lastUpdated;
}
