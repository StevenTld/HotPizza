package com.services;

import com.dtos.CreateOrderDto;
import com.dtos.OrderDto;
import com.entities.OrderStatus;

import java.util.List;

public interface OrderService {

    /**
     * Récupère toutes les commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des commandes
     */
    List<OrderDto> getOrdersByUserId(Long userId);

    /**
     * Récupère les détails d'une commande
     * @param orderId ID de la commande
     * @param userId ID de l'utilisateur (pour vérification)
     * @return Détails de la commande
     */
    OrderDto getOrderById(Long orderId, Long userId);

    /**
     * Crée une commande à partir du panier de l'utilisateur
     * @param userId ID de l'utilisateur
     * @param createOrderDto Informations pour la commande
     * @return La commande créée
     */
    OrderDto createOrderFromCart(Long userId, CreateOrderDto createOrderDto);

    /**
     * Met à jour le statut d'une commande
     * @param orderId ID de la commande
     * @param status Nouveau statut
     * @return La commande mise à jour
     */
    OrderDto updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * Annule une commande
     * @param orderId ID de la commande
     * @param userId ID de l'utilisateur (pour vérification)
     * @return La commande annulée
     */
    OrderDto cancelOrder(Long orderId, Long userId);
}