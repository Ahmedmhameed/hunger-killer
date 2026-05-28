package com.ahmedkh.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer loyaltyPoints;
    private String dietaryNotes;
    private Set<AddressResponse> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
