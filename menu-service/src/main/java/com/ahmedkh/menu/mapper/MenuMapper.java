package com.ahmedkh.menu.mapper;

import com.ahmedkh.menu.dto.request.CategoryRequest;
import com.ahmedkh.menu.dto.request.MenuItemRequest;
import com.ahmedkh.menu.dto.response.CategoryResponse;
import com.ahmedkh.menu.dto.response.MenuItemResponse;
import com.ahmedkh.menu.entity.Category;
import com.ahmedkh.menu.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    CategoryResponse toResponse(Category category);
    
    Category toEntity(CategoryRequest request);
    
    void updateEntity(CategoryRequest request, @MappingTarget Category category);

    @Mapping(target = "categoryId", source = "category.id")
    MenuItemResponse toResponse(MenuItem menuItem);
    
    @Mapping(target = "category.id", source = "categoryId")
    MenuItem toEntity(MenuItemRequest request);
    
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntity(MenuItemRequest request, @MappingTarget MenuItem menuItem);
}
