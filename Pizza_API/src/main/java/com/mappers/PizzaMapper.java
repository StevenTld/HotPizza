package com.mappers;

import com.dtos.PizzaDto;
import com.entities.Pizza;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class PizzaMapper {

    public PizzaDto toDto(Pizza pizza) {

        if(pizza == null) {
            return null;
        }

        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setId(pizza.getId());
        pizzaDto.setName(pizza.getName());
        pizzaDto.setComposition(pizza.getComposition());
        pizzaDto.setPhoto(pizza.getPhoto());
        pizzaDto.setPrix(pizza.getPrix());
        return pizzaDto;
    }

    public Pizza toEntity(PizzaDto pizzaDto) {
        if(pizzaDto == null) {
            return null;
        }

        Pizza pizza = new Pizza();
        if(pizzaDto.getId() != null){
            pizza.setId(pizzaDto.getId());
        }
        pizza.setName(pizzaDto.getName());
        pizza.setComposition(pizzaDto.getComposition());
        pizza.setPhoto(pizzaDto.getPhoto());
        pizza.setPrix(pizzaDto.getPrix());
        return pizza;
    }
    
}
