package com.ahmedkh.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "Label is required")
    @Size(min = 2, max = 50, message = "Label must be between 2 and 50 characters")
    private String label;

    @NotBlank(message = "Street is required")
    @Size(min = 5, max = 200, message = "Street must be between 5 and 200 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    private Double gpsLat;

    private Double gpsLng;
}
