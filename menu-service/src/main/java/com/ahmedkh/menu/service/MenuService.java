package com.ahmedkh.menu.service;

import com.ahmedkh.menu.dto.request.CategoryRequest;
import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.CategoryResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MenuService {
    
    // Category APIs
    CategoryResponse createCategory(CategoryRequest request);
    Page<CategoryResponse> getCategoriesByRestaurant(String restaurantId, Pageable pageable);
    CategoryResponse updateCategory(UUID categoryId, CategoryRequest request);
    void deleteCategory(UUID categoryId);

    // MenuItem APIs
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItem(UUID itemId);
    Page<MenuItemResponse> getMenuItemsByRestaurant(String restaurantId, Pageable pageable);
    MenuItemResponse updateMenuItem(UUID itemId, MenuItemRequest request);
    void deleteMenuItem(UUID itemId);
    void toggleAvailability(UUID itemId);
}
