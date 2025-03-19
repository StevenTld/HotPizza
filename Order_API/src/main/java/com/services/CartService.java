package com.services;

import com.dtos.AddCartItemDto;
import com.dtos.CartDto;
import com.dtos.CartItemDto;

public interface CartService {

    /**
     * Récupère ou crée le panier d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Le panier de l'utilisateur
     */
    CartDto getCartByUserId(Long userId);

    /**
     * Ajoute un ingrédient au panier
     * @param userId ID de l'utilisateur
     * @param itemDto Détails de l'élément à ajouter
     * @return Le panier mis à jour
     */
    CartDto addItemToCart(Long userId, AddCartItemDto itemDto);

    /**
     * Met à jour la quantité d'un élément du panier
     * @param userId ID de l'utilisateur
     * @param itemId ID de l'élément à mettre à jour
     * @param quantity Nouvelle quantité
     * @return Le panier mis à jour
     */
    CartDto updateCartItemQuantity(Long userId, Long itemId, Integer quantity);

    /**
     * Supprime un élément du panier
     * @param userId ID de l'utilisateur
     * @param itemId ID de l'élément à supprimer
     * @return Le panier mis à jour
     */
    CartDto removeItemFromCart(Long userId, Long itemId);

    /**
     * Vide le panier d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Le panier vide
     */
    CartDto clearCart(Long userId);
}