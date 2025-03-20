package com.mappers;

import com.dtos.ExtraIngredientDto;
import com.dtos.OrderDto;
import com.dtos.CreateOrderDto;
import com.dtos.PizzaItemDto;
import com.entities.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class OrderMapper {
    public Order toEntity(CreateOrderDto createOrderDto) {
        Order order = new Order();
        order.setUserId(createOrderDto.getUserId());

        // Vérifier que pizzaItems n'est pas null
        if (createOrderDto.getPizzaItems() != null) {
            for (PizzaItemDto pizzaItem : createOrderDto.getPizzaItems()) {
                // Ajouter la pizza au panier
                order.getPizzaItems().put(pizzaItem.getPizzaId(), pizzaItem.getQuantity());

                // Vérifier que extraIngredients n'est pas null avant d'itérer
                if (pizzaItem.getExtraIngredients() != null && !pizzaItem.getExtraIngredients().isEmpty()) {
                    for (ExtraIngredientDto extra : pizzaItem.getExtraIngredients()) {
                        String key = pizzaItem.getPizzaId() + ":" + extra.getIngredientId();
                        order.getExtraIngredients().put(key, extra.getQuantity());
                    }
                }
            }
        }

        return order;
    }

    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();

        // Copier les propriétés simples
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());


        // Convertir la map des pizzas en liste d'objets PizzaItemDto
        List<PizzaItemDto> pizzaItems = new ArrayList<>();

        // Extraire les pizzaIds et leurs quantités
        for (Map.Entry<Long, Integer> entry : order.getPizzaItems().entrySet()) {
            Long pizzaId = entry.getKey();
            Integer quantity = entry.getValue();

            PizzaItemDto pizzaItemDto = new PizzaItemDto();
            pizzaItemDto.setPizzaId(pizzaId);
            pizzaItemDto.setQuantity(quantity);

            // Trouver tous les ingrédients supplémentaires pour cette pizza
            List<ExtraIngredientDto> extraIngredients = new ArrayList<>();

            // Parcourir les ingrédients supplémentaires (format clé: "pizzaId:ingredientId")
            for (Map.Entry<String, Integer> extraEntry : order.getExtraIngredients().entrySet()) {
                String key = extraEntry.getKey();
                Integer extraQuantity = extraEntry.getValue();

                // Vérifier si cet ingrédient appartient à la pizza courante
                if (key.startsWith(pizzaId + ":")) {
                    // Extraire l'ID de l'ingrédient de la clé composite
                    String[] parts = key.split(":");
                    if (parts.length == 2) {
                        Long ingredientId = Long.valueOf(parts[1]);

                        ExtraIngredientDto extraDto = new ExtraIngredientDto();
                        extraDto.setIngredientId(ingredientId);
                        extraDto.setQuantity(extraQuantity);

                        extraIngredients.add(extraDto);
                    }
                }
            }

            // Ajouter les ingrédients supplémentaires à la pizza
            pizzaItemDto.setExtraIngredients(extraIngredients);

            // Ajouter la pizza à la liste
            pizzaItems.add(pizzaItemDto);
        }

        dto.setPizzaItems(pizzaItems);

        return dto;
    }
}