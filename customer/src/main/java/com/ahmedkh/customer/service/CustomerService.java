package com.ahmedkh.customer.service;

import com.ahmedkh.customer.dto.request.RegisterRequest;
import com.ahmedkh.customer.dto.request.LoginRequest;
import com.ahmedkh.customer.dto.request.UpdateCustomerRequest;
import com.ahmedkh.customer.dto.response.CustomerResponse;
import com.ahmedkh.customer.dto.response.LoginResponse;

import java.util.UUID;

public interface CustomerService {
    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    CustomerResponse getCustomer(UUID customerId);

    CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request);

    void deleteCustomer(UUID customerId);
}
