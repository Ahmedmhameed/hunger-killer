package com.ahmedkh.menu.repository;

import com.ahmedkh.menu.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);
}
