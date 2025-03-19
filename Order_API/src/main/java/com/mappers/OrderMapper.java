package com.mappers;

import com.dtos.OrderDto;
import com.dtos.OrderItemDto;
import com.entities.Order;
import com.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    /**
     * Convertit une entité Order en DTO
     */
    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setSubtotal(order.getSubtotal());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setPickupTime(order.getPickupTime());
        dto.setNotes(order.getNotes());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentTransactionId(order.getPaymentTransactionId());
        dto.setPaid(order.getPaid());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Conversion des items
        if (order.getItems() != null) {
            dto.setItems(
                    order.getItems().stream()
                            .map(this::toItemDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /**
     * Convertit une entité OrderItem en DTO
     */
    public OrderItemDto toItemDto(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setPizzaId(item.getIngredientId());
        dto.setPizzaName(item.getIngredientName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());

        return dto;
    }
}