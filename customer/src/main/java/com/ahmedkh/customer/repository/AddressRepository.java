package com.ahmedkh.customer.repository;

import com.ahmedkh.customer.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomerIdAndDeletedFalse(UUID customerId);

    Optional<Address> findByIdAndDeletedFalse(UUID id);

    Optional<Address> findByIdAndCustomerIdAndDeletedFalse(UUID id, UUID customerId);

    void deleteByIdAndCustomerId(UUID id, UUID customerId);
}
