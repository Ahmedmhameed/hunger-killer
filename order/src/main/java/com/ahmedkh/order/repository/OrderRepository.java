package com.ahmedkh.order.repository;

import com.ahmedkh.order.entity.Order;
import com.ahmedkh.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdAndDeletedFalseOrderByPlacedAtDesc(String customerId);
    Optional<Order> findByIdAndDeletedFalse(UUID id);
    boolean existsByCartIdAndStatusNot(String cartId, OrderStatus status);
}
