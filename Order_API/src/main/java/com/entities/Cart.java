package com.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "carts")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Stockage des pizzas dans le panier
    @ElementCollection
    @CollectionTable(name = "cart_pizza_items", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "pizza_id")
    @Column(name = "quantity")
    private Map<Long, Integer> pizzaItems = new HashMap<>();

    // Stockage des ingrédients supplémentaires au format "pizzaId:ingredientId"
    @ElementCollection
    @CollectionTable(name = "cart_pizza_extras", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "pizza_ingredient_key") // Clé composite "pizzaId:ingredientId"
    @Column(name = "quantity")
    private Map<String, Integer> extraIngredients = new HashMap<>();

    @Column(nullable = false)
    private Double total = 0.0;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }


}