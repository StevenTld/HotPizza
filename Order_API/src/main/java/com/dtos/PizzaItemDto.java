package com.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class PizzaItemDto {
    private Long pizzaId;
    private Integer quantity;
    private List<ExtraIngredientDto> extraIngredients = new ArrayList<>();
    // Ces champs seront remplis à partir de l'API des pizzas (non stockés dans l'entité)
    private String name;
    private Double unitPrice;
}
