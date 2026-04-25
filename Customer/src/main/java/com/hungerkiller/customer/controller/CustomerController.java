package com.hungerkiller.customer.controller;

import com.hungerkiller.customer.dto.CustomerResponse;
import com.hungerkiller.customer.dto.RegisterCustomerRequest;
import com.hungerkiller.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer API", description = "Customer identity and profile management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String sayHello(){
        return "Hello world";
    }
    @PostMapping("/register")
    @Operation(
            summary = "Register a new customer",
            description = "Creates customer account and publishes "
                    + "CustomerRegisteredEvent to Kafka topic 'customer.registered'")
    public ResponseEntity<CustomerResponse> register(
            @Valid @RequestBody RegisterCustomerRequest request) {

        CustomerResponse response = customerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    @Operation(
            summary = "Get customer by ID",
            description = "Returns lightweight shared model — passwordHash "
                    + "and internal tokens are never exposed")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable String customerId) {

        return ResponseEntity.ok(customerService.getById(customerId));
    }
}
