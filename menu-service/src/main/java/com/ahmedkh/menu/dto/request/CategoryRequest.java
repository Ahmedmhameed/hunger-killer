package com.ahmedkh.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotBlank(message = "Name is required")
    private String name;

    private Integer displayOrder;
}
