package com.ahmedkh.menu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items", indexes = {
        @Index(name = "idx_restaurant_id", columnList = "restaurant_id"),
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_is_available", columnList = "is_available"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MenuItem extends BaseEntity {

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "preparation_time_minutes")
    private Integer preparationTimeMinutes;
}
