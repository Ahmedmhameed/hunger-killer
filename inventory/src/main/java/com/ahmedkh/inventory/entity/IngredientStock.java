package com.ahmedkh.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredient_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientStock extends BaseEntity {

    @Column(name = "item_id", nullable = false, length = 36)
    private String itemId;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(name = "ingredient_name", length = 255)
    private String ingredientName;

    @Column(name = "quantity_available", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityAvailable;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "reorder_level", precision = 10, scale = 2)
    private BigDecimal reorderLevel;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
