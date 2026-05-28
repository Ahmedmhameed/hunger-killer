package com.ahmedkh.order.feign.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private String customerId;
    private String restaurantId;
    private BigDecimal totalAmount;
    private String status;
    private List<CartItemDto> items;
}
