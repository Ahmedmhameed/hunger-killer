package com.ahmedkh.menu.service.impl;

import com.ahmedkh.menu.dto.request.CategoryRequest;
import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.CategoryResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.entity.Category;
import com.ahmedkh.menu.entity.MenuItem;
import com.ahmedkh.menu.exception.ResourceNotFoundException;
import com.ahmedkh.menu.mapper.MenuMapper;
import com.ahmedkh.menu.repository.CategoryRepository;
import com.ahmedkh.menu.repository.MenuItemRepository;
import com.ahmedkh.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuMapper menuMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = menuMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return menuMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategoriesByRestaurant(String restaurantId, Pageable pageable) {
        return categoryRepository.findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
                .map(menuMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        menuMapper.updateEntity(request, category);
        return menuMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menuItemsByRestaurant", key = "#request.restaurantId", condition = "#request.restaurantId != null")
    })
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem menuItem = menuMapper.toEntity(request);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + request.getCategoryId()));
            menuItem.setCategory(category);
        }
        
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return menuMapper.toResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menuItem", key = "#itemId")
    public MenuItemResponse getMenuItem(UUID itemId) {
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .filter(item -> !item.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + itemId));
        return menuMapper.toResponse(menuItem);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menuItemsByRestaurant", key = "#restaurantId")
    public Page<MenuItemResponse> getMenuItemsByRestaurant(String restaurantId, Pageable pageable) {
        return menuItemRepository.findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
                .map(menuMapper::toResponse);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menuItem", key = "#itemId"),
            @CacheEvict(value = "menuItemsByRestaurant", allEntries = true)
    })
    public MenuItemResponse updateMenuItem(UUID itemId, MenuItemRequest request) {
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + itemId));
        
        menuMapper.updateEntity(request, menuItem);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + request.getCategoryId()));
            menuItem.setCategory(category);
        } else {
            menuItem.setCategory(null);
        }
        
        return menuMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menuItem", key = "#itemId"),
            @CacheEvict(value = "menuItemsByRestaurant", allEntries = true)
    })
    public void deleteMenuItem(UUID itemId) {
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + itemId));
        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menuItem", key = "#itemId"),
            @CacheEvict(value = "menuItemsByRestaurant", allEntries = true)
    })
    public void toggleAvailability(UUID itemId) {
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id " + itemId));
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        menuItemRepository.save(menuItem);
    }
}
