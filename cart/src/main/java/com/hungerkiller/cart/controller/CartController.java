package com.hungerkiller.cart.controller;

import com.hungerkiller.cart.dto.AddItemRequest;
import com.hungerkiller.cart.dto.CartResponse;
import com.hungerkiller.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart API",
        description = "Shopping cart management. Validates items against Menu MS via sync REST.")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/items")
    @Operation(
            summary = "Add item to cart",
            description = "Calls Menu MS synchronously (REST GET) to validate "
                    + "price and availability before accepting the item")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String customerId,
            @Valid @RequestBody AddItemRequest request) {

        return ResponseEntity.ok(cartService.addItem(customerId, request));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get active cart for a customer")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable String customerId) {

        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @DeleteMapping("/{customerId}/items/{cartItemId}")
    @Operation(summary = "Remove a specific item from cart")
    public ResponseEntity<Void> removeItem(
            @PathVariable String customerId,
            @PathVariable String cartItemId) {

        cartService.removeItem(customerId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<Void> clearCart(
            @PathVariable String customerId) {

        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
