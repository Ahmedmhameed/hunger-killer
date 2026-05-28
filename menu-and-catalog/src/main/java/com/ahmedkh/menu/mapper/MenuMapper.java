package com.ahmedkh.menu.mapper;

import com.ahmedkh.menu.dto.response.CategoryResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.entity.Category;
import com.ahmedkh.menu.entity.MenuItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {
    MenuItemResponse toMenuItemResponse(MenuItem entity);

    MenuItem toMenuItemEntity(MenuItemResponse dto);

    CategoryResponse toCategoryResponse(Category entity);

    Category toCategoryEntity(CategoryResponse dto);
}
