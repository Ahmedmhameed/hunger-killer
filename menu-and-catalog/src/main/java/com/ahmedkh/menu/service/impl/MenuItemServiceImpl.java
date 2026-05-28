package com.ahmedkh.menu.service.impl;

import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.entity.MenuItem;
import com.ahmedkh.menu.exception.ResourceNotFoundException;
import com.ahmedkh.menu.mapper.MenuMapper;
import com.ahmedkh.menu.repository.MenuItemRepository;
import com.ahmedkh.menu.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuMapper menuMapper;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, MenuMapper menuMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menu_items", key = "#itemId")
    public MenuItemResponse getMenuItemById(UUID itemId) {
        log.debug("Fetching menu item with ID: {}", itemId);
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));
        return menuMapper.toMenuItemResponse(menuItem);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant_menus", key = "#restaurantId")
    public List<MenuItemResponse> getMenuByRestaurant(String restaurantId) {
        log.debug("Fetching menu for restaurant: {}", restaurantId);
        return menuItemRepository.findByRestaurantIdAndDeletedFalse(restaurantId)
                .stream()
                .map(menuMapper::toMenuItemResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuByRestaurantPaginated(String restaurantId, Pageable pageable) {
        log.debug("Fetching paginated menu for restaurant: {} with pageable: {}", restaurantId, pageable);
        return menuItemRepository.findPagedByRestaurantId(restaurantId, pageable)
                .map(menuMapper::toMenuItemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuByCategory(String categoryId) {
        log.debug("Fetching menu items for category: {}", categoryId);
        return menuItemRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .stream()
                .map(menuMapper::toMenuItemResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = {"restaurant_menus", "menu_items"}, allEntries = true)
    public MenuItemResponse createMenuItem(String restaurantId, MenuItemRequest request) {
        log.info("Creating menu item for restaurant: {}", restaurantId);
        MenuItem menuItem = MenuItem.builder()
                .restaurantId(restaurantId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .isAvailable(true)
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully: {}", saved.getId());
        return menuMapper.toMenuItemResponse(saved);
    }

    @Override
    @CacheEvict(value = {"restaurant_menus", "menu_items"}, allEntries = true)
    public MenuItemResponse updateMenuItem(UUID itemId, MenuItemRequest request) {
        log.info("Updating menu item: {}", itemId);
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        menuItem.setCategoryId(request.getCategoryId());

        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Menu item updated successfully: {}", itemId);
        return menuMapper.toMenuItemResponse(updated);
    }

    @Override
    @CacheEvict(value = {"restaurant_menus", "menu_items"}, allEntries = true)
    public void deleteMenuItem(UUID itemId) {
        log.info("Deleting menu item: {}", itemId);
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));

        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);
        log.info("Menu item deleted successfully: {}", itemId);
    }

    @Override
    @CacheEvict(value = {"restaurant_menus", "menu_items"}, allEntries = true)
    public void updateMenuItemAvailability(String itemId, boolean isAvailable) {
        log.info("Updating availability for menu item: {} to {}", itemId, isAvailable);
        try {
            UUID uuid = UUID.fromString(itemId);
            MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemId));

            menuItem.setIsAvailable(isAvailable);
            menuItemRepository.save(menuItem);
            log.info("Menu item availability updated: {}", itemId);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid menu item ID format: {}", itemId);
        }
    }
}
