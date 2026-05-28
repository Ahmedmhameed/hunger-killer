package com.ahmedkh.kitchen.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "kitchen_ticket_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenTicketItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private KitchenTicket ticket;

    @Column(name = "item_id", nullable = false, length = 36)
    private String itemId;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "station", nullable = false)
    @Enumerated(EnumType.STRING)
    private StationType station;
}
