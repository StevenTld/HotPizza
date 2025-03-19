package com.repositories;

import com.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Trouve le panier actif d'un utilisateur
     */
    Optional<Cart> findByUserId(Long userId);
}