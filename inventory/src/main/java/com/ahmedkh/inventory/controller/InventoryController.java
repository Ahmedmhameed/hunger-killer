package com.ahmedkh.inventory.controller;

import com.ahmedkh.inventory.dto.request.IngredientStockRequest;
import com.ahmedkh.inventory.dto.request.PurchaseOrderRequest;
import com.ahmedkh.inventory.dto.response.ApiResponse;
import com.ahmedkh.inventory.dto.response.IngredientStockResponse;
import com.ahmedkh.inventory.dto.response.PurchaseOrderResponse;
import com.ahmedkh.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Check stock for an item", description = "Internal/Public endpoint to get item stock")
    @GetMapping("/stock/{itemId}")
    public ResponseEntity<ApiResponse<IngredientStockResponse>> getStockForItem(@PathVariable String itemId) {
        IngredientStockResponse response = inventoryService.getStockForItem(itemId);
        return ResponseEntity.ok(ApiResponse.ok("Stock retrieved successfully", response));
    }

    @Operation(summary = "View all stock for a restaurant")
    @GetMapping("/stock/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<List<IngredientStockResponse>>> getStockForRestaurant(@PathVariable String restaurantId) {
        List<IngredientStockResponse> response = inventoryService.getStockForRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.ok("Stock list retrieved successfully", response));
    }

    @Operation(summary = "Update stock quantity manually")
    @PutMapping("/stock/{stockId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<IngredientStockResponse>> updateStockQuantity(
            @PathVariable UUID stockId, @Valid @RequestBody IngredientStockRequest request) {
        IngredientStockResponse response = inventoryService.updateStockQuantity(stockId, request);
        return ResponseEntity.ok(ApiResponse.ok("Stock quantity updated", response));
    }

    @Operation(summary = "Create a purchase order")
    @PostMapping("/purchase-orders")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse response = inventoryService.createPurchaseOrder(request);
        return ResponseEntity.ok(ApiResponse.ok("Purchase order created", response));
    }
}
