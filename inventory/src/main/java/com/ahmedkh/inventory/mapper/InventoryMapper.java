package com.ahmedkh.inventory.mapper;

import com.ahmedkh.inventory.dto.request.PurchaseOrderRequest;
import com.ahmedkh.inventory.dto.response.IngredientStockResponse;
import com.ahmedkh.inventory.dto.response.PurchaseOrderResponse;
import com.ahmedkh.inventory.entity.IngredientStock;
import com.ahmedkh.inventory.entity.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    IngredientStockResponse toResponse(IngredientStock stock);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    PurchaseOrderResponse toResponse(PurchaseOrder po);

    @Mapping(target = "ingredient.id", source = "ingredientId")
    PurchaseOrder toEntity(PurchaseOrderRequest request);
}
