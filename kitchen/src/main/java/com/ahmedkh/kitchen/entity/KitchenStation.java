package com.ahmedkh.kitchen.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "kitchen_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenStation extends BaseEntity {

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "station_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StationType stationType;

    @Column(name = "current_load", nullable = false)
    @Builder.Default
    private Integer currentLoad = 0;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    public void incrementLoad() {
        this.currentLoad++;
    }

    public void decrementLoad() {
        if (this.currentLoad > 0) {
            this.currentLoad--;
        }
    }

    public boolean hasCapacity() {
        return currentLoad < maxCapacity;
    }
}
