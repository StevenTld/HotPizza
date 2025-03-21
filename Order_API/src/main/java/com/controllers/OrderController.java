package com.controllers;

import com.dtos.OrderDto;
import com.dtos.CreateOrderDto;
import com.services.OrderServiceImpl;
import com.utils.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            OrderDto orderDto = orderService.getOrderByIdForUser(id, userId);
            return ResponseEntity.ok(orderDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getCurrentUserOrders() {
        try {
            Long userId = UserUtil.getCurrentUserId();
            List<OrderDto> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            // Vérifier que l'utilisateur ne peut pas créer une commande pour quelqu'un d'autre
            if (!userId.equals(createOrderDto.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            OrderDto createdOrder = orderService.createOrder(createOrderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            orderService.deleteOrderForUser(id, userId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            OrderDto updatedOrder = orderService.updateOrderStatus(id, status, userId);
            return ResponseEntity.ok(updatedOrder);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}