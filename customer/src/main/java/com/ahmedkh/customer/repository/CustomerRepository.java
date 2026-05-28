package com.ahmedkh.customer.repository;

import com.ahmedkh.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmailAndDeletedFalse(String email);

    Optional<Customer> findByPhoneAndDeletedFalse(String phone);

    Optional<Customer> findByIdAndDeletedFalse(UUID id);

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.deleted = false")
    Optional<Customer> findActiveCustomerById(@Param("id") UUID id);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByPhoneAndDeletedFalse(String phone);
}
