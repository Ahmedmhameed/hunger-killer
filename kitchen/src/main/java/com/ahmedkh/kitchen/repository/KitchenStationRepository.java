package com.ahmedkh.kitchen.repository;

import com.ahmedkh.kitchen.entity.KitchenStation;
import com.ahmedkh.kitchen.entity.StationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KitchenStationRepository extends JpaRepository<KitchenStation, UUID> {
    Optional<KitchenStation> findByIdAndDeletedFalse(UUID id);

    List<KitchenStation> findByRestaurantIdAndDeletedFalse(String restaurantId);
    List<KitchenStation> findByRestaurantIdAndStationTypeAndDeletedFalse(
            String restaurantId,
            StationType stationType);}
