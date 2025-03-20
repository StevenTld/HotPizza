package com.controllers;

import com.dtos.CartDto;
import com.services.CartServiceImpl;
import com.utils.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart") // Notez le singulier - c'est le panier de l'utilisateur connecté
@CrossOrigin(origins = "*")
public class CartController {
    private final CartServiceImpl cartService;

    @Autowired
    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    // Récupérer le panier de l'utilisateur connecté
    @GetMapping
    public ResponseEntity<CartDto> getCurrentUserCart() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto cartDto = cartService.getOrCreateCartForUser(userId);
            return ResponseEntity.ok(cartDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Ajouter une pizza au panier
    @PostMapping("/pizzas")
    public ResponseEntity<CartDto> addPizzaToCart(
            @RequestParam Long pizzaId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto updatedCart = cartService.addPizzaToUserCart(userId, pizzaId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Ajouter un ingrédient supplémentaire à une pizza
    @PostMapping("/pizzas/{pizzaId}/extras")
    public ResponseEntity<CartDto> addExtraIngredientToCart(
            @PathVariable Long pizzaId,
            @RequestParam Long ingredientId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto updatedCart = cartService.addExtraIngredientToUserCart(userId, pizzaId, ingredientId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Supprimer une pizza du panier
    @DeleteMapping("/pizzas/{pizzaId}")
    public ResponseEntity<CartDto> removePizzaFromCart(@PathVariable Long pizzaId) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto updatedCart = cartService.removePizzaFromUserCart(userId, pizzaId);
            return ResponseEntity.ok(updatedCart);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un ingrédient supplémentaire du panier
    @DeleteMapping("/pizzas/{pizzaId}/extras/{ingredientId}")
    public ResponseEntity<CartDto> removeExtraIngredientFromCart(
            @PathVariable Long pizzaId,
            @PathVariable Long ingredientId) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto updatedCart = cartService.removeExtraIngredientFromUserCart(userId, pizzaId, ingredientId);
            return ResponseEntity.ok(updatedCart);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Vider le panier
    @DeleteMapping("/clear")
    public ResponseEntity<CartDto> clearCart() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            CartDto emptyCart = cartService.clearUserCart(userId);
            return ResponseEntity.ok(emptyCart);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}