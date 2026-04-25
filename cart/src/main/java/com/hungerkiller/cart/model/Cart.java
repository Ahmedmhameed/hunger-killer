package com.hungerkiller.cart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;

    @Column(nullable = false)
    private String customerId;

    private String restaurantId;

    // Cart expires after 30 minutes of inactivity
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cart",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(30);
    }

    public Double getTotalAmount() {
        return items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
    }
}
