package com.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice = 0.0;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    // Méthode utilitaire pour calculer le prix total de l'item
    public void calculateTotalPrice() {
        this.totalPrice = this.unitPrice * this.quantity;
    }

    @PrePersist
    @PreUpdate
    protected void onSaveOrUpdate() {
        calculateTotalPrice();
    }
}