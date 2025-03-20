package com.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Stockage des pizzas commandées
    @ElementCollection
    @CollectionTable(name = "order_pizza_items", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "pizza_id")
    @Column(name = "quantity")
    private Map<Long, Integer> pizzaItems = new HashMap<>();

    // Stockage des ingrédients supplémentaires
    @ElementCollection
    @CollectionTable(name = "order_pizza_extras", joinColumns = @JoinColumn(name = "order_id"))
    private Map<String, Integer> extraIngredients = new HashMap<>();


    @Column(nullable = false)
    private Double total = 0.0;

    @Column(name = "payment_status")
    private Boolean paid = false;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

}