package com.services;

import com.dtos.CartDto;
import com.entities.Cart;
import com.mappers.CartMapper;
import com.repositories.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("CartService")
@Transactional
public class CartServiceImpl {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    // Récupérer ou créer le panier d'un utilisateur
    public CartDto getOrCreateCartForUser(Long userId) {
        try {
            // Essayer de récupérer le panier unique
            Cart cart = cartRepository.findByUserId(userId);
            return cartMapper.toDto(cart);
        } catch (NonUniqueResultException e) {
            // Si plusieurs paniers existent, utiliser findAllByUserId et prendre le premier
            List<Cart> carts = cartRepository.findAllByUserId(userId);
            if (!carts.isEmpty()) {
                // Utiliser le premier panier trouvé
                Cart cart = carts.get(0);

                // Optionnel: supprimer les autres paniers
                for (int i = 1; i < carts.size(); i++) {
                    cartRepository.delete(carts.get(i));
                }

                return cartMapper.toDto(cart);
            } else {
                // Cas improbable mais géré: aucun panier trouvé malgré l'exception
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart = cartRepository.save(newCart);
                return cartMapper.toDto(newCart);
            }
        } catch (Exception e) {
            // Si aucun panier n'existe (ou autre erreur), en créer un nouveau
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart = cartRepository.save(newCart);
            return cartMapper.toDto(newCart);
        }
    }

    // Ajouter une pizza au panier de l'utilisateur
    public CartDto addPizzaToUserCart(Long userId, Long pizzaId, Integer quantity) {
        // Récupérer ou créer le panier
        Cart cart = getOrCreateCartByUserId(userId);

        // Ajouter ou mettre à jour la quantité de la pizza
        Integer currentQuantity = cart.getPizzaItems().getOrDefault(pizzaId, 0);
        cart.getPizzaItems().put(pizzaId, currentQuantity + quantity);

        // Recalculer le total (à implémenter avec un service externe qui connaît les prix)
        // cart.setTotal(calculateTotal(cart));

        // Sauvegarder le panier
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDto(updatedCart);
    }

    // Ajouter un ingrédient supplémentaire à une pizza dans le panier de l'utilisateur
    public CartDto addExtraIngredientToUserCart(Long userId, Long pizzaId, Long ingredientId, Integer quantity) {
        // Récupérer le panier
        Cart cart = getOrCreateCartByUserId(userId);

        // Vérifier que la pizza existe dans le panier
        if (!cart.getPizzaItems().containsKey(pizzaId)) {
            throw new IllegalArgumentException("La pizza doit d'abord être ajoutée au panier");
        }

        // Créer la clé composite
        String key = pizzaId + ":" + ingredientId;

        // Ajouter ou mettre à jour la quantité de l'ingrédient
        Integer currentQuantity = cart.getExtraIngredients().getOrDefault(key, 0);
        cart.getExtraIngredients().put(key, currentQuantity + quantity);

        // Recalculer le total
        // cart.setTotal(calculateTotal(cart));

        // Sauvegarder le panier
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDto(updatedCart);
    }

    // Supprimer une pizza du panier de l'utilisateur
    public CartDto removePizzaFromUserCart(Long userId, Long pizzaId) {
        // Récupérer le panier
        Cart cart = getCartByUserId(userId);

        // Supprimer la pizza
        cart.getPizzaItems().remove(pizzaId);

        // Supprimer également tous les ingrédients supplémentaires associés à cette pizza
        cart.getExtraIngredients().entrySet().removeIf(entry -> entry.getKey().startsWith(pizzaId + ":"));

        // Recalculer le total
        // cart.setTotal(calculateTotal(cart));

        // Sauvegarder le panier
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDto(updatedCart);
    }

    // Supprimer un ingrédient supplémentaire du panier de l'utilisateur
    public CartDto removeExtraIngredientFromUserCart(Long userId, Long pizzaId, Long ingredientId) {
        // Récupérer le panier
        Cart cart = getCartByUserId(userId);

        // Créer la clé composite
        String key = pizzaId + ":" + ingredientId;

        // Supprimer l'ingrédient
        cart.getExtraIngredients().remove(key);

        // Recalculer le total
        // cart.setTotal(calculateTotal(cart));

        // Sauvegarder le panier
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDto(updatedCart);
    }

    // Vider le panier de l'utilisateur
    public CartDto clearUserCart(Long userId) {
        // Récupérer le panier
        Cart cart = getCartByUserId(userId);

        // Vider les collections
        cart.getPizzaItems().clear();
        cart.getExtraIngredients().clear();
        cart.setTotal(0.0);

        // Sauvegarder le panier
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDto(updatedCart);
    }

    // Méthode utilitaire pour récupérer le panier d'un utilisateur
    private Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new EntityNotFoundException("Panier non trouvé pour l'utilisateur: " + userId);
        }
        return cart;
    }

    // Méthode utilitaire pour récupérer ou créer le panier d'un utilisateur
    private Cart getOrCreateCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }
        return cart;
    }
}