package com.ahmedkh.kitchen.repository;

import com.ahmedkh.kitchen.entity.KitchenTicket;
import com.ahmedkh.kitchen.entity.KitchenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KitchenTicketRepository extends JpaRepository<KitchenTicket, UUID> {
    Optional<KitchenTicket> findByOrderIdAndDeletedFalse(String orderId);

    Optional<KitchenTicket> findByIdAndDeletedFalse(UUID id);

    List<KitchenTicket> findByRestaurantIdAndStatusAndDeletedFalse(String restaurantId, KitchenStatus status);

    List<KitchenTicket> findByRestaurantIdAndDeletedFalse(String restaurantId);

    @Query("SELECT kt FROM KitchenTicket kt WHERE kt.restaurantId = :restaurantId AND kt.status != 'READY' AND kt.status != 'CANCELED' AND kt.deleted = false")
    List<KitchenTicket> findActiveTicketsByRestaurant(@Param("restaurantId") String restaurantId);

    @Query("SELECT COUNT(kt) FROM KitchenTicket kt WHERE kt.restaurantId = :restaurantId AND kt.status = :status AND kt.deleted = false")
    long countByRestaurantAndStatus(@Param("restaurantId") String restaurantId, @Param("status") KitchenStatus status);
}
