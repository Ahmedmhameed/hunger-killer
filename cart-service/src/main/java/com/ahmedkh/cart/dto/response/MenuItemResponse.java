package com.ahmedkh.cart.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MenuItemResponse {
    private String id;
    private String restaurantId;
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;
    private Integer preparationTimeMinutes;
}
