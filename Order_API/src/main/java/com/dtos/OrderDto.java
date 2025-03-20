package com.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private List<PizzaItemDto> pizzaItems;


}