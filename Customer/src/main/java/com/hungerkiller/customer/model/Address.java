package com.hungerkiller.customer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String addressId;

    private String label;    // e.g. Home, Work, Hotel

    private String street;

    private String city;

    private Double gpsLat;

    private Double gpsLng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

   }