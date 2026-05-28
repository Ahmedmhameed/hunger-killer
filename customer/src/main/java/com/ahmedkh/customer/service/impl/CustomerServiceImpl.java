package com.ahmedkh.customer.service.impl;

import com.ahmedkh.customer.dto.request.RegisterRequest;
import com.ahmedkh.customer.dto.request.LoginRequest;
import com.ahmedkh.customer.dto.request.UpdateCustomerRequest;
import com.ahmedkh.customer.dto.response.CustomerResponse;
import com.ahmedkh.customer.dto.response.LoginResponse;
import com.ahmedkh.customer.entity.Customer;
import com.ahmedkh.customer.exception.BusinessException;
import com.ahmedkh.customer.exception.ResourceNotFoundException;
import com.ahmedkh.customer.kafka.CustomerEventProducer;
import com.ahmedkh.customer.mapper.CustomerMapper;
import com.ahmedkh.customer.repository.CustomerRepository;
import com.ahmedkh.customer.service.CustomerService;
import com.ahmedkh.customer.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomerEventProducer eventProducer;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                              CustomerMapper customerMapper,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil,
                              CustomerEventProducer eventProducer) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.eventProducer = eventProducer;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());

        if (customerRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        if (customerRepository.existsByPhoneAndDeletedFalse(request.getPhone())) {
            throw new BusinessException("Phone number already registered");
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer registered successfully with ID: {}", savedCustomer.getId());

        // Publish event
        eventProducer.publishCustomerRegisteredEvent(savedCustomer);

        String token = jwtUtil.generateToken(savedCustomer.getId(), savedCustomer.getEmail());

        return LoginResponse.builder()
                .customerId(savedCustomer.getId())
                .email(savedCustomer.getEmail())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Customer login attempt with email: {}", request.getEmail());

        Customer customer = customerRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(customer.getId(), customer.getEmail());
        log.info("Customer logged in successfully: {}", customer.getId());

        return LoginResponse.builder()
                .customerId(customer.getId())
                .email(customer.getEmail())
                .token(token)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(UUID customerId) {
        log.debug("Fetching customer with ID: {}", customerId);

        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            if (!customer.getPhone().equals(request.getPhone()) &&
                    customerRepository.existsByPhoneAndDeletedFalse(request.getPhone())) {
                throw new BusinessException("Phone number already in use");
            }
            customer.setPhone(request.getPhone());
        }
        if (request.getDietaryNotes() != null) {
            customer.setDietaryNotes(request.getDietaryNotes());
        }

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", customerId);

        return customerMapper.toResponse(updated);
    }

    @Override
    public void deleteCustomer(UUID customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        customer.setDeleted(true);
        customerRepository.save(customer);
        log.info("Customer deleted (soft delete): {}", customerId);
    }
}
