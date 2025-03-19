package com.controllers;

import com.dtos.CreateOrderDto;
import com.dtos.OrderDto;
import com.entities.OrderStatus;
import com.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Récupère toutes les commandes de l'utilisateur courant
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        // NOTE: Dans une application réelle, l'ID utilisateur serait récupéré à partir du contexte de sécurité
        Long userId = 1L; // À remplacer par l'obtention de l'ID utilisateur depuis le token JWT

        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les détails d'une commande spécifique
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable Long orderId) {
        // NOTE: Dans une application réelle, l'ID utilisateur serait récupéré à partir du contexte de sécurité
        Long userId = 1L; // À remplacer par l'obtention de l'ID utilisateur depuis le token JWT

        OrderDto order = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(order);
    }

    /**
     * Crée une nouvelle commande à partir du panier
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        // NOTE: Dans une application réelle, l'ID utilisateur serait récupéré à partir du contexte de sécurité
        Long userId = 1L; // À remplacer par l'obtention de l'ID utilisateur depuis le token JWT

        OrderDto newOrder = orderService.createOrderFromCart(userId, createOrderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * Met à jour le statut d'une commande (admin seulement)
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        // NOTE: Dans une application réelle, il faudrait vérifier que l'utilisateur a un rôle ADMIN

        OrderDto updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Annule une commande
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId) {
        // NOTE: Dans une application réelle, l'ID utilisateur serait récupéré à partir du contexte de sécurité
        Long userId = 1L; // À remplacer par l'obtention de l'ID utilisateur depuis le token JWT

        OrderDto cancelledOrder = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(cancelledOrder);
    }
}