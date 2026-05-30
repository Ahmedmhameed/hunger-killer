package com.ahmedkh.notification.repository;

import com.ahmedkh.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByDeletedFalseOrderByCreatedAtDesc();

    List<Notification> findByCustomerIdAndDeletedFalseOrderByCreatedAtDesc(String customerId);

    List<Notification> findByOrderIdAndDeletedFalseOrderByCreatedAtDesc(String orderId);

    java.util.Optional<Notification> findByIdAndDeletedFalse(UUID id);
}
