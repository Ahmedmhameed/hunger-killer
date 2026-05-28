package com.ahmedkh.customer.entity;

import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "label", nullable = false, length = 50)
    private String label;

    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "gps_lat", precision = 10, scale = 8)
    private Double gpsLat;

    @Column(name = "gps_lng", precision = 11, scale = 8)
    private Double gpsLng;
}
