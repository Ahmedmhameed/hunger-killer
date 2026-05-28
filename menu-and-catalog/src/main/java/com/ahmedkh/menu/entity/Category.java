package com.ahmedkh.menu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_restaurant_id", columnList = "restaurant_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Category extends BaseEntity {

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;
}
