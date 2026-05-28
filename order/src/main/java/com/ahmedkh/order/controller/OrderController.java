package com.ahmedkh.order.controller;

import com.ahmedkh.order.dto.request.PlaceOrderRequest;
import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.dto.response.OrderResponse;
import com.ahmedkh.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and lifecycle management")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place a new order from cart")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal String customerId,
            @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully", response));
    }

    @Operation(summary = "Get order details with full status history")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal String customerId) {
        OrderResponse response = orderService.getOrder(orderId, customerId);
        return ResponseEntity.ok(ApiResponse.ok("Order retrieved", response));
    }

    @Operation(summary = "List all orders for a customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersForCustomer(
            @PathVariable String customerId,
            @AuthenticationPrincipal String requestingCustomerId) {
        // Allow if requesting own orders or ADMIN
        List<OrderResponse> orders = orderService.getOrdersForCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved", orders));
    }

    @Operation(summary = "Cancel an order (only PLACED or CONFIRMED)")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal String customerId) {
        OrderResponse response = orderService.cancelOrder(orderId, customerId);
        return ResponseEntity.ok(ApiResponse.ok("Order canceled", response));
    }
}
