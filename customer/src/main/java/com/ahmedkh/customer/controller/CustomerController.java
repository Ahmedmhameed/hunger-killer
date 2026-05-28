package com.ahmedkh.customer.controller;

import com.ahmedkh.customer.dto.request.AddressRequest;
import com.ahmedkh.customer.dto.request.RegisterRequest;
import com.ahmedkh.customer.dto.request.LoginRequest;
import com.ahmedkh.customer.dto.request.UpdateCustomerRequest;
import com.ahmedkh.customer.dto.response.AddressResponse;
import com.ahmedkh.customer.dto.response.ApiResponse;
import com.ahmedkh.customer.dto.response.CustomerResponse;
import com.ahmedkh.customer.dto.response.LoginResponse;
import com.ahmedkh.customer.service.AddressService;
import com.ahmedkh.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@Validated
@Tag(name = "Customer Service", description = "APIs for customer management, authentication, and addresses")
public class CustomerController {

    private final CustomerService customerService;
    private final AddressService addressService;

    public CustomerController(CustomerService customerService, AddressService addressService) {
        this.customerService = customerService;
        this.addressService = addressService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new customer", description = "Create a new customer account with email and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register endpoint called for email: {}", request.getEmail());
        LoginResponse response = customerService.register(request);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Customer registered successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Login customer", description = "Authenticate customer with email and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called for email: {}", request.getEmail());
        LoginResponse response = customerService.login(request);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer details", description = "Retrieve customer profile by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @PathVariable @NotNull UUID customerId) {
        log.info("Get customer endpoint called for ID: {}", customerId);
        CustomerResponse response = customerService.getCustomer(customerId);
        ApiResponse<CustomerResponse> apiResponse = ApiResponse.<CustomerResponse>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer details", description = "Update customer profile information")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable @NotNull UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        log.info("Update customer endpoint called for ID: {}", customerId);
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        ApiResponse<CustomerResponse> apiResponse = ApiResponse.<CustomerResponse>builder()
                .success(true)
                .message("Customer updated successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer account", description = "Soft delete customer account")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Customer deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable @NotNull UUID customerId) {
        log.info("Delete customer endpoint called for ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/addresses")
    @Operation(summary = "Add address", description = "Add a new address to customer account")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address added"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @PathVariable @NotNull UUID customerId,
            @Valid @RequestBody AddressRequest request) {
        log.info("Add address endpoint called for customer: {}", customerId);
        AddressResponse response = addressService.addAddress(customerId, request);
        ApiResponse<AddressResponse> apiResponse = ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address added successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{customerId}/addresses")
    @Operation(summary = "Get all addresses", description = "Retrieve all addresses for a customer")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @PathVariable @NotNull UUID customerId) {
        log.info("Get addresses endpoint called for customer: {}", customerId);
        List<AddressResponse> response = addressService.getAddresses(customerId);
        ApiResponse<List<AddressResponse>> apiResponse = ApiResponse.<List<AddressResponse>>builder()
                .success(true)
                .message("Addresses retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Delete address", description = "Delete an address from customer account")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Address deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address or customer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteAddress(
            @PathVariable @NotNull UUID customerId,
            @PathVariable @NotNull UUID addressId) {
        log.info("Delete address endpoint called for address: {} of customer: {}", addressId, customerId);
        addressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
