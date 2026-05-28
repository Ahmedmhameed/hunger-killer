package com.ahmedkh.cart.repository;

import com.ahmedkh.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByCustomerIdAndDeletedFalse(String customerId);
    List<Cart> findByExpiresAtBeforeAndStatusNotAndDeletedFalse(LocalDateTime now, com.ahmedkh.cart.entity.CartStatus status);
}
