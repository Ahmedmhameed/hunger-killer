package com.ahmedkh.menu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MenuItemResponse {
    private UUID id;
    private String restaurantId;
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;
    private Integer preparationTimeMinutes;
}
