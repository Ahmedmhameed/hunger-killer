package com.ahmedkh.menu.repository;

import com.ahmedkh.menu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByIdAndDeletedFalse(UUID id);

    List<Category> findByRestaurantIdAndDeletedFalse(String restaurantId);

    Optional<Category> findByRestaurantIdAndNameAndDeletedFalse(String restaurantId, String name);
}
