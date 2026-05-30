package com.ahmedkh.delivery.repository;

import com.ahmedkh.delivery.entity.Delivery;
import com.ahmedkh.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByOrderId(String orderId);

    List<Delivery> findByCustomerId(String customerId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByDriverId(String driverId);

    List<Delivery> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}
