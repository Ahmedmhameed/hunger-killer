package com.ahmedkh.cart.client.fallback;

import com.ahmedkh.cart.client.MenuServiceClient;
import com.ahmedkh.cart.dto.response.ApiResponse;
import com.ahmedkh.cart.dto.response.MenuItemResponse;
import com.ahmedkh.cart.exception.BusinessException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MenuServiceClientFallbackFactory implements FallbackFactory<MenuServiceClient> {

    @Override
    public MenuServiceClient create(Throwable cause) {
        return new MenuServiceClient() {
            @Override
            public ApiResponse<MenuItemResponse> getItemById(String itemId) {
                log.error("Error communicating with menu-service for item {}: {}", itemId, cause.getMessage());
                if (cause instanceof FeignException.NotFound) {
                    throw new com.ahmedkh.cart.exception.ResourceNotFoundException("Menu item not found: " + itemId);
                }
                throw new BusinessException("Menu service is currently unavailable. Please try again later.");
            }
        };
    }
}
