package com.services;

import com.dtos.OrderDto;
import com.dtos.CreateOrderDto;
import com.entities.Order;
import com.mappers.OrderMapper;
import com.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("OrderService")
@Transactional
public class OrderServiceImpl {
    // Dépendances, constructeur, etc.
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + id));

        return orderMapper.toDto(order);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto createOrder(CreateOrderDto createOrderDto) {
        // Convertir le DTO en entité
        Order order = orderMapper.toEntity(createOrderDto);

        // Sauvegarder l'entité en base de données
        Order savedOrder = orderRepository.save(order);

        // Convertir l'entité sauvegardée en DTO pour la réponse
        return orderMapper.toDto(savedOrder);
    }


    public void deleteOrder(Long id) {
        // Vérifier si la commande existe
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Commande non trouvée avec l'ID: " + id);
        }

        // Supprimer la commande
        orderRepository.deleteById(id);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        // Récupérer les commandes de l'utilisateur
        List<Order> userOrders = orderRepository.findByUserId(userId);

        // Convertir les entités en DTOs
        return userOrders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }


    public OrderDto getOrderByIdForUser(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + id));
        if(!order.getUserId().equals(userId)){
            throw new SecurityException("Vous n'êtes pas autorisé à accéder à cette commande");
        }
        return orderMapper.toDto(order);
    }

    public void deleteOrderForUser(Long id, Long userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + id));
        if(!order.getUserId().equals(userId)){
            throw new SecurityException("Vous n'êtes pas autorisé à accéder à cette commande");
        }
        orderRepository.deleteById(id);

    }
}