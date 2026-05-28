package com.ahmedkh.cart.client;

import com.ahmedkh.cart.dto.response.ApiResponse;
import com.ahmedkh.cart.dto.response.MenuItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", fallbackFactory = com.ahmedkh.cart.client.fallback.MenuServiceClientFallbackFactory.class)
public interface MenuServiceClient {

    @GetMapping("/api/v1/menu/items/{itemId}")
    ApiResponse<MenuItemResponse> getItemById(@PathVariable("itemId") String itemId);
}
