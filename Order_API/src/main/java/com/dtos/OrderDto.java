package com.dtos;

import com.entities.OrderStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private List<OrderItemDto> items = new ArrayList<>();
    private Double subtotal;
    private Double total;
    private OrderStatus status;
    private Date pickupTime;
    private String notes;
    private String paymentMethod;
    private String paymentTransactionId;
    private Boolean paid;
    private Date createdAt;
    private Date updatedAt;
}