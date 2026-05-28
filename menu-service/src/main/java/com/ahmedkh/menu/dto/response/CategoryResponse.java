package com.ahmedkh.menu.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID id;
    private String restaurantId;
    private String name;
    private Integer displayOrder;
}
