package com.ahmedkh.inventory.service.impl;

import com.ahmedkh.inventory.dto.request.IngredientStockRequest;
import com.ahmedkh.inventory.dto.request.PurchaseOrderRequest;
import com.ahmedkh.inventory.dto.response.IngredientStockResponse;
import com.ahmedkh.inventory.dto.response.PurchaseOrderResponse;
import com.ahmedkh.inventory.entity.IngredientStock;
import com.ahmedkh.inventory.entity.PurchaseOrder;
import com.ahmedkh.inventory.entity.PurchaseOrderStatus;
import com.ahmedkh.inventory.exception.BusinessException;
import com.ahmedkh.inventory.exception.ResourceNotFoundException;
import com.ahmedkh.inventory.kafka.InventoryEventPublisher;
import com.ahmedkh.inventory.mapper.InventoryMapper;
import com.ahmedkh.inventory.repository.IngredientStockRepository;
import com.ahmedkh.inventory.repository.PurchaseOrderRepository;
import com.ahmedkh.inventory.service.InventoryService;
import com.hungerkiller.events.StockDepletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final IngredientStockRepository ingredientStockRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryEventPublisher inventoryEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public IngredientStockResponse getStockForItem(String itemId) {
        IngredientStock stock = ingredientStockRepository.findByItemIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for item " + itemId));
        return inventoryMapper.toResponse(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientStockResponse> getStockForRestaurant(String restaurantId) {
        return ingredientStockRepository.findByRestaurantIdAndDeletedFalse(restaurantId)
                .stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IngredientStockResponse updateStockQuantity(UUID stockId, IngredientStockRequest request) {
        IngredientStock stock = ingredientStockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id " + stockId));

        stock.setQuantityAvailable(request.getQuantity());
        stock.setLastUpdated(LocalDateTime.now());
        
        if (stock.getQuantityAvailable().compareTo(BigDecimal.ZERO) > 0) {
            stock.setIsAvailable(true);
        } else {
            stock.setIsAvailable(false);
        }

        IngredientStock saved = ingredientStockRepository.save(stock);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        IngredientStock stock = ingredientStockRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id " + request.getIngredientId()));

        PurchaseOrder po = inventoryMapper.toEntity(request);
        po.setRestaurantId(stock.getRestaurantId());
        po.setIngredient(stock);
        po.setStatus(PurchaseOrderStatus.REQUESTED);
        po.setRequestedAt(LocalDateTime.now());

        PurchaseOrder savedPo = purchaseOrderRepository.save(po);
        return inventoryMapper.toResponse(savedPo);
    }

    @Override
    @Transactional
    public void handleOrderPlaced(Map<String, Object> event) {
        String orderId = (String) event.get("orderId");
        String restaurantId = (String) event.get("restaurantId");
        List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");

        if (orderId == null || items == null || items.isEmpty()) {
            log.warn("Invalid order event: missing fields");
            return;
        }

        for (Map<String, Object> itemData : items) {
            String itemId = (String) itemData.get("itemId");
            Number qtyNum = (Number) itemData.get("quantity");
            if (itemId == null || qtyNum == null) continue;
            
            int quantity = qtyNum.intValue();

            ingredientStockRepository.findByItemIdAndDeletedFalse(itemId)
                    .ifPresent(stock -> {
                        BigDecimal newQty = stock.getQuantityAvailable().subtract(BigDecimal.valueOf(quantity));
                        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                            newQty = BigDecimal.ZERO;
                        }
                        stock.setQuantityAvailable(newQty);
                        stock.setLastUpdated(LocalDateTime.now());

                        boolean stockDepleted = false;
                        boolean isOutOfStock = false;

                        if (newQty.compareTo(BigDecimal.ZERO) == 0) {
                            stock.setIsAvailable(false);
                            stockDepleted = true;
                            isOutOfStock = true;
                        } else if (stock.getReorderLevel() != null && newQty.compareTo(stock.getReorderLevel()) <= 0) {
                            stockDepleted = true;
                            isOutOfStock = false;
                        }

                        ingredientStockRepository.save(stock);

                        if (stockDepleted) {
                            StockDepletedEvent depletedEvent = StockDepletedEvent.newBuilder()
                                    .setItemId(stock.getItemId())
                                    .setItemName(stock.getItemName() != null ? stock.getItemName() : "Unknown")
                                    .setRestaurantId(stock.getRestaurantId())
                                    .setRemainingQty(stock.getQuantityAvailable().doubleValue())
                                    .setIsOutOfStock(isOutOfStock)
                                    .setDepleteAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .build();
                            inventoryEventPublisher.publishStockDepletedEvent(depletedEvent);
                        }
                    });
        }
    }
}
