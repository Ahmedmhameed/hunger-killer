package com.ahmedkh.inventory.repository;

import com.ahmedkh.inventory.entity.IngredientStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngredientStockRepository extends JpaRepository<IngredientStock, UUID> {
    List<IngredientStock> findByRestaurantIdAndDeletedFalse(String restaurantId);
    Optional<IngredientStock> findByItemIdAndDeletedFalse(String itemId);
}
