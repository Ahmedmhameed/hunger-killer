package com.ahmedkh.menu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;
}
