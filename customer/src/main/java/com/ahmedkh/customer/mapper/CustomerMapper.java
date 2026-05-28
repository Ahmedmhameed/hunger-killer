package com.ahmedkh.customer.mapper;

import com.ahmedkh.customer.dto.request.AddressRequest;
import com.ahmedkh.customer.dto.request.RegisterRequest;
import com.ahmedkh.customer.dto.response.AddressResponse;
import com.ahmedkh.customer.dto.response.CustomerResponse;
import com.ahmedkh.customer.entity.Address;
import com.ahmedkh.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Customer toEntity(RegisterRequest request);

    @Mapping(target = "addresses", source = "addresses")
    CustomerResponse toResponse(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Address toAddressEntity(AddressRequest request);

    AddressResponse toAddressResponse(Address entity);
}
