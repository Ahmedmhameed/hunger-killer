package com.hungerkiller.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class CartResponse {

    private String cartId;
    private String customerId;
    private String restaurantId;
    private List<CartItemResponse> items;
    private Double totalAmount;
    private LocalDateTime expiresAt;
}
