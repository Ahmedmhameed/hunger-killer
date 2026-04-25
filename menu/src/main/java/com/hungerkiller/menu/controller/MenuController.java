package com.hungerkiller.menu.controller;

import com.hungerkiller.menu.dto.MenuItemRequest;
import com.hungerkiller.menu.dto.MenuItemResponse;
import com.hungerkiller.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@Tag(name = "Menu API", description = "Food items and restaurant catalog management")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Called by Cart MS synchronously via REST.
     * Returns item price + availability — the Cart cannot proceed without this.
     */
    @GetMapping("/items/{itemId}")
    @Operation(
            summary = "Get menu item by ID",
            description = "Used by Cart MS to validate price and availability "
                    + "before adding to cart (sync REST call)")
    public ResponseEntity<MenuItemResponse> getItem(
            @PathVariable String itemId) {
        return ResponseEntity.ok(menuService.getItemById(itemId));
    }

    @GetMapping("/restaurants/{restaurantId}/items")
    @Operation(summary = "Get all items for a restaurant")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(
            @PathVariable String restaurantId) {
        return ResponseEntity.ok(
                menuService.getItemsByRestaurant(restaurantId));
    }

    @PostMapping("/items")
    @Operation(summary = "Create a new menu item (Restaurant Admin)")
    public ResponseEntity<MenuItemResponse> createItem(
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuService.createItem(request));
    }
}