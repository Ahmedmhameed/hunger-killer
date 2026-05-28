package com.ahmedkh.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder extends BaseEntity {

    @Column(name = "restaurant_id", length = 36)
    private String restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private IngredientStock ingredient;

    @Column(name = "supplier_name", length = 255)
    private String supplierName;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit", length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PurchaseOrderStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
