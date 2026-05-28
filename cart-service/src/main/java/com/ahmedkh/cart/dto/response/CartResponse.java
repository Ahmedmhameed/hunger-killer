package com.ahmedkh.cart.dto.response;

import com.ahmedkh.cart.entity.CartStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private UUID id;
    private String customerId;
    private String restaurantId;
    private BigDecimal totalAmount;
    private CartStatus status;
    private LocalDateTime expiresAt;
    private List<CartItemResponse> items;
}
