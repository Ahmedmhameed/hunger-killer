package com.ahmedkh.menu.controller;

import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.ApiResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@Validated
@Tag(name = "Menu Management", description = "APIs for menu and catalog management")
public class MenuController {

    private final MenuItemService menuItemService;

    public MenuController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get menu item", description = "Retrieve a single menu item by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItem(@PathVariable @NotBlank UUID itemId) {
        log.info("Get menu item endpoint called for ID: {}", itemId);
        MenuItemResponse response = menuItemService.getMenuItemById(itemId);
        ApiResponse<MenuItemResponse> apiResponse = ApiResponse.<MenuItemResponse>builder()
                .success(true)
                .message("Menu item retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/restaurants/{restaurantId}/items")
    @Operation(summary = "Get restaurant menu", description = "Retrieve full menu for a restaurant")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Menu retrieved")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getRestaurantMenu(
            @PathVariable @NotBlank String restaurantId) {
        log.info("Get restaurant menu endpoint called for restaurant: {}", restaurantId);
        List<MenuItemResponse> response = menuItemService.getMenuByRestaurant(restaurantId);
        ApiResponse<List<MenuItemResponse>> apiResponse = ApiResponse.<List<MenuItemResponse>>builder()
                .success(true)
                .message("Menu retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/restaurants/{restaurantId}/items/paginated")
    @Operation(summary = "Get paginated menu", description = "Retrieve paginated menu with sorting and filtering")
    public ResponseEntity<ApiResponse<Page<MenuItemResponse>>> getRestaurantMenuPaginated(
            @PathVariable @NotBlank String restaurantId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Get paginated menu endpoint called for restaurant: {}", restaurantId);
        Page<MenuItemResponse> response = menuItemService.getMenuByRestaurantPaginated(restaurantId, pageable);
        ApiResponse<Page<MenuItemResponse>> apiResponse = ApiResponse.<Page<MenuItemResponse>>builder()
                .success(true)
                .message("Paginated menu retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/items")
    @Operation(summary = "Create menu item", description = "Add a new menu item to restaurant")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item created")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMenuItem(
            @RequestHeader(value = "X-Restaurant-Id", required = true) @NotBlank String restaurantId,
            @Valid @RequestBody MenuItemRequest request) {
        log.info("Create menu item endpoint called for restaurant: {}", restaurantId);
        MenuItemResponse response = menuItemService.createMenuItem(restaurantId, request);
        ApiResponse<MenuItemResponse> apiResponse = ApiResponse.<MenuItemResponse>builder()
                .success(true)
                .message("Menu item created successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update menu item", description = "Update an existing menu item")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item updated")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable @NotBlank UUID itemId,
            @Valid @RequestBody MenuItemRequest request) {
        log.info("Update menu item endpoint called for ID: {}", itemId);
        MenuItemResponse response = menuItemService.updateMenuItem(itemId, request);
        ApiResponse<MenuItemResponse> apiResponse = ApiResponse.<MenuItemResponse>builder()
                .success(true)
                .message("Menu item updated successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete menu item", description = "Remove a menu item (soft delete)")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item deleted")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable @NotBlank UUID itemId) {
        log.info("Delete menu item endpoint called for ID: {}", itemId);
        menuItemService.deleteMenuItem(itemId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Menu item deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/items/{itemId}/availability")
    @Operation(summary = "Toggle item availability", description = "Update menu item availability status (internal)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Availability updated")
    public ResponseEntity<ApiResponse<Void>> updateItemAvailability(
            @PathVariable @NotBlank String itemId,
            @RequestParam boolean available) {
        log.info("Update item availability endpoint called for ID: {}, available: {}", itemId, available);
        menuItemService.updateMenuItemAvailability(itemId, available);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Item availability updated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
