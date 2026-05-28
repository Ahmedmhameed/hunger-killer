package com.ahmedkh.inventory.service;

import com.ahmedkh.inventory.dto.request.IngredientStockRequest;
import com.ahmedkh.inventory.dto.request.PurchaseOrderRequest;
import com.ahmedkh.inventory.dto.response.IngredientStockResponse;
import com.ahmedkh.inventory.dto.response.PurchaseOrderResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface InventoryService {

    IngredientStockResponse getStockForItem(String itemId);
    
    List<IngredientStockResponse> getStockForRestaurant(String restaurantId);
    
    IngredientStockResponse updateStockQuantity(UUID stockId, IngredientStockRequest request);
    
    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);

    void handleOrderPlaced(Map<String, Object> event);
}
