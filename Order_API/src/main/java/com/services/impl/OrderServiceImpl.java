package com.services.impl;

import com.dtos.CreateOrderDto;
import com.dtos.OrderDto;
import com.entities.*;
import com.mappers.OrderMapper;
import com.repositories.CartRepository;
import com.repositories.OrderRepository;
import com.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        // Vérifier que l'utilisateur a accès à cette commande
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à accéder à cette commande");
        }

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto createOrderFromCart(Long userId, CreateOrderDto createOrderDto) {
        // Récupérer le panier de l'utilisateur
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé pour l'utilisateur: " + userId));

        // Vérifier que le panier n'est pas vide
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Impossible de créer une commande à partir d'un panier vide");
        }

        // Créer une nouvelle commande
        Order order = new Order();
        order.setUserId(userId);

        // Définir l'heure de retrait si présente
        if (createOrderDto.getPickupTime() != null) {
            order.setPickupTime(createOrderDto.getPickupTime());
        }

        // Définir les notes si présentes
        if (createOrderDto.getNotes() != null) {
            order.setNotes(createOrderDto.getNotes());
        }

        // Définir la méthode de paiement
        order.setPaymentMethod(createOrderDto.getPaymentMethod());

        // Convertir les éléments du panier en éléments de commande
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(cartItem);
            order.addItem(orderItem);
        }

        // Calculer les totaux et enregistrer la commande
        order.calculateTotals();
        order = orderRepository.save(order);

        // Vider le panier après avoir créé la commande
        cart.getItems().clear();
        cart.setTotal(0.0);
        cartRepository.save(cart);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        // Vérifier la logique de transition d'état (règles métier)
        validateStatusTransition(order.getStatus(), status);

        // Mettre à jour le statut
        order.setStatus(status);

        // Si le statut est PAID, mettre à jour le champ paid
        if (status == OrderStatus.PAID) {
            order.setPaid(true);
        }

        // Si le statut est CANCELLED, réinitialiser le champ paid
        if (status == OrderStatus.CANCELLED) {
            order.setPaid(false);
        }

        // Enregistrer la commande mise à jour
        order = orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + orderId));

        // Vérifier que l'utilisateur a accès à cette commande
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à annuler cette commande");
        }

        // Vérifier que la commande peut être annulée
        if (!canBeCancelled(order.getStatus())) {
            throw new IllegalStateException("La commande ne peut plus être annulée dans son état actuel: " + order.getStatus());
        }

        // Mettre à jour le statut
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaid(false);

        // Enregistrer la commande mise à jour
        order = orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    // Méthodes utilitaires privées

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Règles de transition d'état à implémenter selon la logique métier
        // Exemple simple: on ne peut pas revenir à un état antérieur dans le processus
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de changer l'état d'une commande annulée");
        }

        // Exemple: une commande prête ne peut plus être modifiée
        if (currentStatus == OrderStatus.READY_FOR_DELIVERY && newStatus != OrderStatus.READY_FOR_DELIVERY
                && newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Impossible de modifier une commande déjà prête à être récupérée");
        }

        // Autres règles à définir selon les besoins métier...
    }

    private boolean canBeCancelled(OrderStatus status) {
        // Une commande peut être annulée si elle est en état CREATED, PAID ou PREPARING
        return status == OrderStatus.CREATED ||
                status == OrderStatus.PAID ||
                status == OrderStatus.PREPARING;
    }
}