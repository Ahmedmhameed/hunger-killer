package com.hungerkiller.customer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    // Hidden model — never exposed in any response DTO (DDD principle)
    private String passwordHash;

    private LocalDate dateOfBirth;

    private String dietaryNotes;

    private int loyaltyPoints = 0;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private final List<Address> addresses = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void adjestLoyaltyPoints(int amount){
        loyaltyPoints += amount;
    }
  }