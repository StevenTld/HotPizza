package com.services.impl;

import com.dtos.AddCartItemDto;
import com.dtos.CartDto;
import com.entities.Cart;
import com.entities.CartItem;
import com.mappers.CartMapper;
import com.repositories.CartItemRepository;
import com.repositories.CartRepository;
import com.services.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(Long userId) {
        // Récupère le panier de l'utilisateur ou en crée un nouveau s'il n'existe pas
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto addItemToCart(Long userId, AddCartItemDto itemDto) {
        // Récupère le panier de l'utilisateur
        Cart cart = getOrCreateCart(userId);

        // Vérifie si l'article existe déjà dans le panier
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getIngredientId().equals(itemDto.getPizzaId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Met à jour la quantité si l'article existe déjà
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + itemDto.getQuantity());
            existingItem.calculateTotalPrice();
        } else {
            // Crée un nouvel article si l'article n'existe pas encore
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setIngredientId(itemDto.getPizzaId());
            newItem.setIngredientName(itemDto.getPizzaName());
            newItem.setQuantity(itemDto.getQuantity());
            newItem.setUnitPrice(itemDto.getUnitPrice());
            newItem.calculateTotalPrice();

            cart.addItem(newItem);
        }

        // Recalcule le total et sauvegarde
        cart.calculateTotal();
        cart = cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto updateCartItemQuantity(Long userId, Long itemId, Integer quantity) {
        // Vérifie que la quantité est positive
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        // Récupère le panier
        Cart cart = getCartOrThrow(userId);

        // Trouve l'article dans le panier
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Article non trouvé dans le panier"));

        // Met à jour la quantité
        item.setQuantity(quantity);
        item.calculateTotalPrice();

        // Recalcule le total et sauvegarde
        cart.calculateTotal();
        cart = cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto removeItemFromCart(Long userId, Long itemId) {
        // Récupère le panier
        Cart cart = getCartOrThrow(userId);

        // Trouve l'article à supprimer
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Article non trouvé dans le panier"));

        // Supprime l'article
        cart.removeItem(itemToRemove);

        // Recalcule le total et sauvegarde
        cart.calculateTotal();
        cart = cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto clearCart(Long userId) {
        // Récupère le panier
        Cart cart = getCartOrThrow(userId);

        // Vide le panier
        cart.getItems().clear();
        cart.setTotal(0.0);

        // Sauvegarde
        cart = cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    // Méthodes utilitaires privées

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    private Cart getCartOrThrow(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé pour l'utilisateur: " + userId));
    }
}