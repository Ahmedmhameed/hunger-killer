package com.ahmedkh.cart.controller;

import com.ahmedkh.cart.dto.request.CartItemRequest;
import com.ahmedkh.cart.dto.request.CartItemUpdateRequest;
import com.ahmedkh.cart.dto.response.ApiResponse;
import com.ahmedkh.cart.dto.response.CartResponse;
import com.ahmedkh.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get current cart for customer")
    @GetMapping("/{customerId}")
    @PreAuthorize("#customerId == authentication.principal or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable String customerId) {
        CartResponse response = cartService.getCart(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Cart retrieved successfully", response));
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/{customerId}/items")
    @PreAuthorize("#customerId == authentication.principal")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @PathVariable String customerId,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse response = cartService.addItem(customerId, request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", response));
    }

    @Operation(summary = "Update item quantity in cart")
    @PutMapping("/{customerId}/items/{cartItemId}")
    @PreAuthorize("#customerId == authentication.principal")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @PathVariable String customerId,
            @PathVariable UUID cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request) {
        CartResponse response = cartService.updateItemQuantity(customerId, cartItemId, request);
        return ResponseEntity.ok(ApiResponse.ok("Cart item updated", response));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/{customerId}/items/{cartItemId}")
    @PreAuthorize("#customerId == authentication.principal")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable String customerId,
            @PathVariable UUID cartItemId) {
        CartResponse response = cartService.removeItem(customerId, cartItemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", response));
    }

    @Operation(summary = "Clear entire cart")
    @DeleteMapping("/{customerId}")
    @PreAuthorize("#customerId == authentication.principal")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable String customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared successfully", null));
    }
    
    @Operation(summary = "Mark cart as checked out (Internal)")
    @PatchMapping("/{customerId}/checkout")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SERVICE') or #customerId == authentication.principal")
    public ResponseEntity<ApiResponse<CartResponse>> checkoutCart(@PathVariable String customerId) {
        CartResponse response = cartService.checkoutCart(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Cart marked as checked out", response));
    }
}
