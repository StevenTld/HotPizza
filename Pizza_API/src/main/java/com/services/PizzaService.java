package com.services;

import com.dtos.PizzaDto;

import java.util.List;

public interface PizzaService {

    PizzaDto savePizza(PizzaDto pizzaDto);

    PizzaDto getPizzaById(Long id);

    boolean deletePizza(Long id);

    List<PizzaDto> getAllPizza();
}
