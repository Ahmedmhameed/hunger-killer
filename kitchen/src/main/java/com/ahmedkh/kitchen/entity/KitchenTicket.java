package com.ahmedkh.kitchen.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "kitchen_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenTicket extends BaseEntity {

    @Column(name = "order_id", nullable = false, unique = true, length = 36)
    private String orderId;

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private KitchenStatus status;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "estimated_ready_at")
    private LocalDateTime estimatedReadyAt;

    @Column(name = "actual_ready_at")
    private LocalDateTime actualReadyAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<KitchenTicketItem> items = new HashSet<>();

    public void addItem(KitchenTicketItem item) {
        items.add(item);
        item.setTicket(this);
    }

    public void removeItem(KitchenTicketItem item) {
        items.remove(item);
        item.setTicket(null);
    }
}
