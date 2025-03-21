package com.repositories;

import com.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Trouver le panier d'un utilisateur
    Cart findByUserId(Long userId);

    // Vérifier si un utilisateur a déjà un panier
    boolean existsByUserId(Long userId);

    List<Cart> findAllByUserId(Long userId);
}