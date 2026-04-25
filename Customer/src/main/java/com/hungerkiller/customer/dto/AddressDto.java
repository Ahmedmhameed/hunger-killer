package com.hungerkiller.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String addressId;
    private String label;
    private String street;
    private String city;
    private Double gpsLat;
    private Double gpsLng;

}
