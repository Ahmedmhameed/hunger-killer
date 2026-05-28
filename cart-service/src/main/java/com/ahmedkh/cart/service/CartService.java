package com.ahmedkh.cart.service;

import com.ahmedkh.cart.dto.request.CartItemRequest;
import com.ahmedkh.cart.dto.request.CartItemUpdateRequest;
import com.ahmedkh.cart.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {
    
    CartResponse getCart(String customerId);
    
    CartResponse addItem(String customerId, CartItemRequest request);
    
    CartResponse updateItemQuantity(String customerId, UUID cartItemId, CartItemUpdateRequest request);
    
    CartResponse removeItem(String customerId, UUID cartItemId);
    
    void clearCart(String customerId);
    
    CartResponse checkoutCart(String customerId);
}
