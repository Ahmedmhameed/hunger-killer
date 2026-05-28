package com.ahmedkh.menu.service;

import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MenuItemService {
    MenuItemResponse getMenuItemById(UUID itemId);

    List<MenuItemResponse> getMenuByRestaurant(String restaurantId);

    Page<MenuItemResponse> getMenuByRestaurantPaginated(String restaurantId, Pageable pageable);

    List<MenuItemResponse> getMenuByCategory(String categoryId);

    MenuItemResponse createMenuItem(String restaurantId, MenuItemRequest request);

    MenuItemResponse updateMenuItem(UUID itemId, MenuItemRequest request);

    void deleteMenuItem(UUID itemId);

    void updateMenuItemAvailability(String itemId, boolean isAvailable);
}
