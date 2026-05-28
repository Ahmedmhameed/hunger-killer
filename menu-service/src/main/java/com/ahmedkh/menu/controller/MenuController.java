package com.ahmedkh.menu.controller;

import com.ahmedkh.menu.dto.request.CategoryRequest;
import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.ApiResponse;
import com.ahmedkh.menu.dto.response.CategoryResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu management endpoints")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "Get single menu item", description = "Public endpoint used by UI and Cart Service via Feign")
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getItemById(@PathVariable UUID itemId) {
        MenuItemResponse response = menuService.getMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.ok("Menu item retrieved successfully", response));
    }

    @Operation(summary = "Get full menu for a restaurant", description = "Public endpoint with pagination")
    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getMenuByRestaurant(
            @PathVariable String restaurantId, Pageable pageable) {
        Page<MenuItemResponse> response = menuService.getMenuItemsByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Menu items retrieved successfully", response));
    }

    @Operation(summary = "Add a new menu item")
    @PostMapping("/items")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MenuItemResponse>> addMenuItem(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuService.createMenuItem(request);
        return ResponseEntity.ok(ApiResponse.ok("Menu item created", response));
    }

    @Operation(summary = "Update a menu item")
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable UUID itemId, @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuService.updateMenuItem(itemId, request);
        return ResponseEntity.ok(ApiResponse.ok("Menu item updated", response));
    }

    @Operation(summary = "Remove a menu item")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable UUID itemId) {
        menuService.deleteMenuItem(itemId);
        return ResponseEntity.ok(ApiResponse.ok("Menu item deleted", null));
    }

    @Operation(summary = "Toggle availability of an item (Internal)")
    @PatchMapping("/items/{itemId}/availability")
    @PreAuthorize("hasRole('SERVICE') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> toggleAvailability(@PathVariable UUID itemId) {
        menuService.toggleAvailability(itemId);
        return ResponseEntity.ok(ApiResponse.ok("Availability toggled", null));
    }

    // --- Category endpoints ---

    @Operation(summary = "Get categories for a restaurant")
    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategoriesByRestaurant(
            @PathVariable String restaurantId, Pageable pageable) {
        Page<CategoryResponse> response = menuService.getCategoriesByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved successfully", response));
    }

    @Operation(summary = "Create a category")
    @PostMapping("/categories")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = menuService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.ok("Category created", response));
    }

    @Operation(summary = "Update a category")
    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID categoryId, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = menuService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.ok("Category updated", response));
    }

    @Operation(summary = "Remove a category")
    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID categoryId) {
        menuService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted", null));
    }
}
