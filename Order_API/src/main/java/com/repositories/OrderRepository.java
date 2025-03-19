package com.repositories;

import com.entities.Order;
import com.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Trouve toutes les commandes d'un utilisateur
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Trouve les commandes d'un utilisateur avec un statut spécifique
     */
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
}