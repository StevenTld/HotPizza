package com.repositories;

import com.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Trouve toutes les commandes d'un utilisateur
     */
    List<Order> findByUserId(Long userId);


}