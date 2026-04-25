package com.hungerkiller.customer.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CustomerResponse {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int loyaltyPoints;
    private String dietaryNotes;
    private LocalDateTime createdAt;
    private List<AddressDto> addresses;

}
