package com.hungerkiller.menu.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String restaurantId;

    @Column(nullable = false)
    private String name;

    private String location;

    private String cuisineType;

    private String workingHours;   // e.g. "08:00-23:00"

    private Double rating;

    @OneToMany(mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();



}
