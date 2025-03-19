package com.controllers;

import com.dtos.AddCartItemDto;
import com.dtos.CartDto;
import com.services.CartService;
import com.utils.UserUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Récupère le panier de l'utilisateur courant
     */
    @GetMapping
    public ResponseEntity<CartDto> getCurrentCart() {
        // NOTE: Dans une application réelle, l'ID utilisateur serait récupéré à partir du contexte de sécurité
        // Pour les besoins de démonstration, nous utilisons un ID utilisateur fixe
        Long userId = UserUtil.getCurrentUserId();

        CartDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Ajoute un article au panier
     */
    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(@Valid @RequestBody AddCartItemDto itemDto) {
        Long userId = UserUtil.getCurrentUserId();

        CartDto updatedCart = cartService.addItemToCart(userId, itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    /**
     * Met à jour la quantité d'un article dans le panier
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        Long userId = UserUtil.getCurrentUserId();

        CartDto updatedCart = cartService.updateCartItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Supprime un article du panier
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long itemId) {
        Long userId = UserUtil.getCurrentUserId();

        CartDto updatedCart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Vide le panier
     */
    @DeleteMapping
    public ResponseEntity<CartDto> clearCart() {
        Long userId = UserUtil.getCurrentUserId();

        CartDto emptyCart = cartService.clearCart(userId);
        return ResponseEntity.ok(emptyCart);
    }
}