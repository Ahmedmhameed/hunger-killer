package com.hungerkiller.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemResponse {

    private String cartItemId;
    private String itemId;
    private String itemName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;
    private String specialNotes;

}
