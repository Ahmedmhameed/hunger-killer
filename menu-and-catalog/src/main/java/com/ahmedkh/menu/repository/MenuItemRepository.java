package com.ahmedkh.menu.repository;

import com.ahmedkh.menu.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    Optional<MenuItem> findByIdAndDeletedFalse(UUID id);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deleted = false ORDER BY m.name ASC")
    List<MenuItem> findByRestaurantIdAndDeletedFalse(@Param("restaurantId") String restaurantId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.categoryId = :categoryId AND m.deleted = false ORDER BY m.name ASC")
    List<MenuItem> findByRestaurantIdAndCategoryIdAndDeletedFalse(
            @Param("restaurantId") String restaurantId,
            @Param("categoryId") String categoryId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.deleted = false")
    Page<MenuItem> findPagedByRestaurantId(@Param("restaurantId") String restaurantId, Pageable pageable);

    List<MenuItem> findByCategoryIdAndDeletedFalse(String categoryId);
}
