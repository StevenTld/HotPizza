package com.dtos;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDto {
    private Long userId;
    private List<PizzaItemDto> pizzaItems;

}
