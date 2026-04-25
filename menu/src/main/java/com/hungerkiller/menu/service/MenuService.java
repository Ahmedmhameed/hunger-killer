package com.hungerkiller.menu.service;

import com.hungerkiller.menu.dto.MenuItemRequest;
import com.hungerkiller.menu.dto.MenuItemResponse;
import com.hungerkiller.menu.model.Category;
import com.hungerkiller.menu.model.MenuItem;
import com.hungerkiller.menu.repository.CategoryRepository;
import com.hungerkiller.menu.repository.MenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuItemRepository menuItemRepository,
                       CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }


    public MenuItemResponse getItemById(String itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found: " + itemId));
        return toResponse(item);
    }

    public List<MenuItemResponse> getItemsByRestaurant(String restaurantId) {
        return menuItemRepository
                .findByCategory_Restaurant_RestaurantId(restaurantId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MenuItemResponse createItem(MenuItemRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found: " + req.getCategoryId()));

        MenuItem item = new MenuItem();
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setImageUrl(req.getImageUrl());
        item.setCategory(category);
        item.setIsAvailable(true);

        return toResponse(menuItemRepository.save(item));
    }

    // ── Mapper ────────────────────────────────────────────────────────
    private MenuItemResponse toResponse(MenuItem i) {
        MenuItemResponse r = new MenuItemResponse();
        r.setItemId(i.getItemId());
        r.setName(i.getName());
        r.setDescription(i.getDescription());
        r.setPrice(i.getPrice());
        r.setImageUrl(i.getImageUrl());
        r.setIsAvailable(i.getIsAvailable());
        r.setIsFeatured(i.getIsFeatured());
        if (i.getCategory() != null) {
            r.setCategoryId(i.getCategory().getCategoryId());
            r.setCategoryName(i.getCategory().getName());
        }
        return r;
    }
}