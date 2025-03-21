package com.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CartDto {
    private Long id;
    private Long userId;
    private List<PizzaItemDto> pizzaItems = new ArrayList<>();
    private Double total;
    private Date createdAt;
}