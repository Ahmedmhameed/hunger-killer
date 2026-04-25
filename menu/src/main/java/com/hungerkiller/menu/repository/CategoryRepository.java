package com.hungerkiller.menu.repository;

import com.hungerkiller.menu.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, String> {

    List<Category> findByRestaurant_RestaurantId(String restaurantId);
}