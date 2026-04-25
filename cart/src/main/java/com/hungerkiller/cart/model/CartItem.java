package com.hungerkiller.cart.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartItemId;

    @Column(nullable = false)
    private String itemId;          // Reference to Menu MS

    @Column(nullable = false)
    private String itemName;        // Snapshot at time of add

    @Column(nullable = false)
    private Double unitPrice;       // Snapshot at time of add (price lock)

    @Column(nullable = false)
    private Integer quantity;

    private String specialNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public Double getSubtotal() {
        return unitPrice * quantity;
    }
}
