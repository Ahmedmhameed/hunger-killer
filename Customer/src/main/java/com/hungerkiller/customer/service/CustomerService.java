package com.hungerkiller.customer.service;

import com.hungerkiller.customer.dto.*;
import com.hungerkiller.customer.kafka.CustomerEventProducer;
import com.hungerkiller.customer.kafka.CustomerRegisteredEvent;
import com.hungerkiller.customer.model.Address;
import com.hungerkiller.customer.model.Customer;
import com.hungerkiller.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerEventProducer eventProducer;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerEventProducer eventProducer) {
        this.customerRepository = customerRepository;
        this.eventProducer      = eventProducer;
    }

    public CustomerResponse register(RegisterCustomerRequest req) {

        // Check if email already exists
        if (customerRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        // Build entity
        Customer customer = new Customer();
        customer.setFirstName(req.getFirstName());
        customer.setLastName(req.getLastName());
        customer.setEmail(req.getEmail());
        customer.setPhone(req.getPhone());
        // In production: hash password with BCrypt
        customer.setPasswordHash("hashed_" + req.getPassword());
        customer.setDateOfBirth(req.getDateOfBirth());
        customer.setDietaryNotes(req.getDietaryNotes());

        // Map addresses
        if (req.getAddresses() != null) {
            List<Address> addresses = req.getAddresses().stream()
                    .map(dto -> {
                        Address a = new Address();
                        a.setLabel(dto.getLabel());
                        a.setStreet(dto.getStreet());
                        a.setCity(dto.getCity());
                        a.setGpsLat(dto.getGpsLat());
                        a.setGpsLng(dto.getGpsLng());
                        a.setCustomer(customer);
                        return a;
                    })
                    .toList();
            customer.getAddresses().addAll(addresses);
        }

        Customer saved = customerRepository.save(customer);

        // Publish event to Kafka — fire and forget
        eventProducer.publishCustomerRegistered(
                new CustomerRegisteredEvent(
                        saved.getCustomerId(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        saved.getEmail()
                )
        );

        return toResponse(saved);
    }

    public CustomerResponse getById(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found: " + customerId));
        return toResponse(customer);
    }

    // ── Mapper ────────────────────────────────────────────────────────
    private CustomerResponse toResponse(Customer c) {
        CustomerResponse resp = new CustomerResponse();
        resp.setCustomerId(c.getCustomerId());
        resp.setFirstName(c.getFirstName());
        resp.setLastName(c.getLastName());
        resp.setEmail(c.getEmail());
        resp.setPhone(c.getPhone());
        resp.setLoyaltyPoints(c.getLoyaltyPoints());
        resp.setDietaryNotes(c.getDietaryNotes());
        resp.setCreatedAt(c.getCreatedAt());

        if (c.getAddresses() != null) {
            List<AddressDto> dtos = c.getAddresses().stream()
                    .map(a -> {
                        AddressDto dto = new AddressDto();
                        dto.setAddressId(a.getAddressId());
                        dto.setLabel(a.getLabel());
                        dto.setStreet(a.getStreet());
                        dto.setCity(a.getCity());
                        dto.setGpsLat(a.getGpsLat());
                        dto.setGpsLng(a.getGpsLng());
                        return dto;
                    })
                    .collect(Collectors.toList());
            resp.setAddresses(dtos);
        }
        return resp;
    }
}
