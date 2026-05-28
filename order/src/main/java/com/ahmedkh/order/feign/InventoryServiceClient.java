package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.StockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", fallbackFactory = InventoryServiceClientFallbackFactory.class)
public interface InventoryServiceClient {

    @GetMapping("/api/v1/inventory/stock/{itemId}")
    ApiResponse<StockDto> getStockForItem(@PathVariable String itemId);
}
