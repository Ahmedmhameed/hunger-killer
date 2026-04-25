package com.hungerkiller.cart.repository;

import com.hungerkiller.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByCustomerId(String customerId);

    void deleteByCustomerId(String customerId);
}