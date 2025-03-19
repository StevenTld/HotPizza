package com.dtos;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long pizzaId;
    private String pizzaName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}