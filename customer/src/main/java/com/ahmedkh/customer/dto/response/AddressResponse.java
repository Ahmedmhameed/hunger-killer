package com.ahmedkh.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {
    private UUID id;
    private String label;
    private String street;
    private String city;
    private Double gpsLat;
    private Double gpsLng;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
