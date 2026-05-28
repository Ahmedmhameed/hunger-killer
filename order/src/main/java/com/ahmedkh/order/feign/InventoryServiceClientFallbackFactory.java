package com.ahmedkh.order.feign;

import com.ahmedkh.order.dto.response.ApiResponse;
import com.ahmedkh.order.feign.dto.StockDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClientFallbackFactory.class);

    @Override
    public InventoryServiceClient create(Throwable cause) {
        return itemId -> {
            log.error("InventoryService getStockForItem fallback for item {}: {}", itemId, cause.getMessage());
            throw new RuntimeException("Inventory service unavailable: " + cause.getMessage());
        };
    }
}
