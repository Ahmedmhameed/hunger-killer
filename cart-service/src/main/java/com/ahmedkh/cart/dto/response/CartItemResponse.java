package com.ahmedkh.cart.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID id;
    private String itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String specialNotes;
}
