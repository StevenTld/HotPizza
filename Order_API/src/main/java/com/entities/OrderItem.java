package com.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

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

    // Constructeur par défaut requis par JPA
    public OrderItem() {
    }

    // Constructeur pratique pour créer un OrderItem à partir d'un CartItem
    public OrderItem(CartItem cartItem) {
        this.ingredientId = cartItem.getIngredientId();
        this.ingredientName = cartItem.getIngredientName();
        this.quantity = cartItem.getQuantity();
        this.unitPrice = cartItem.getUnitPrice();
        calculateTotalPrice();
    }
}