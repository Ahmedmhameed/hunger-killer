package com.ahmedkh.customer.service.impl;

import com.ahmedkh.customer.dto.request.AddressRequest;
import com.ahmedkh.customer.dto.response.AddressResponse;
import com.ahmedkh.customer.entity.Address;
import com.ahmedkh.customer.entity.Customer;
import com.ahmedkh.customer.exception.ResourceNotFoundException;
import com.ahmedkh.customer.mapper.CustomerMapper;
import com.ahmedkh.customer.repository.AddressRepository;
import com.ahmedkh.customer.repository.CustomerRepository;
import com.ahmedkh.customer.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public AddressServiceImpl(AddressRepository addressRepository,
                             CustomerRepository customerRepository,
                             CustomerMapper customerMapper) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public AddressResponse addAddress(UUID customerId, AddressRequest request) {
        log.info("Adding address for customer: {}", customerId);

        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        Address address = customerMapper.toAddressEntity(request);
        address.setCustomer(customer);

        Address saved = addressRepository.save(address);
        log.info("Address added successfully for customer: {}", customerId);

        return customerMapper.toAddressResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(UUID customerId) {
        log.debug("Fetching addresses for customer: {}", customerId);

        // Verify customer exists
        customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        return addressRepository.findByCustomerIdAndDeletedFalse(customerId)
                .stream()
                .map(customerMapper::toAddressResponse)
                .toList();
    }

    @Override
    public void deleteAddress(UUID customerId, UUID addressId) {
        log.info("Deleting address: {} for customer: {}", addressId, customerId);

        Address address = addressRepository.findByIdAndCustomerIdAndDeletedFalse(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        address.setDeleted(true);
        addressRepository.save(address);
        log.info("Address deleted (soft delete): {}", addressId);
    }
}
