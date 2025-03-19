package com.repositories;

import com.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Trouve un élément spécifique dans un panier
     */
    Optional<CartItem> findByCartIdAndIngredientId(Long cartId, Long ingredientId);
}