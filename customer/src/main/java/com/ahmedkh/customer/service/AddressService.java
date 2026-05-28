package com.ahmedkh.customer.service;

import com.ahmedkh.customer.dto.request.AddressRequest;
import com.ahmedkh.customer.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse addAddress(UUID customerId, AddressRequest request);

    List<AddressResponse> getAddresses(UUID customerId);

    void deleteAddress(UUID customerId, UUID addressId);
}
