package com.hungerkiller.menu.repository;

import com.hungerkiller.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findByCategory_Restaurant_RestaurantId(String restaurantId);

    List<MenuItem> findByCategory_CategoryId(String categoryId);

    List<MenuItem> findByIsAvailableTrue();
}