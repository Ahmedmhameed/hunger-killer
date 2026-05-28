package com.ahmedkh.menu.repository;

import com.ahmedkh.menu.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    Page<MenuItem> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);
}
