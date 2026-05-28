package com.ahmedkh.menu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    private String categoryId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    private String imageUrl;
    
    private Boolean isAvailable = true;

    private Integer preparationTimeMinutes;
}
