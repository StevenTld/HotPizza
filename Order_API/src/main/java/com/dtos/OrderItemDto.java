package com.dtos;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}