package com.mappers;

import com.dtos.CartDto;
import com.dtos.PizzaItemDto;
import com.dtos.ExtraIngredientDto;
import com.entities.Cart;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CartMapper {

    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto dto = new CartDto();

        // Copier les propriétés simples
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setTotal(cart.getTotal());
        dto.setCreatedAt(cart.getCreatedAt());

        // Convertir la map des pizzas en liste d'objets PizzaItemDto
        List<PizzaItemDto> pizzaItems = new ArrayList<>();

        // Extraire les pizzaIds et leurs quantités
        for (Map.Entry<Long, Integer> entry : cart.getPizzaItems().entrySet()) {
            Long pizzaId = entry.getKey();
            Integer quantity = entry.getValue();

            PizzaItemDto pizzaItemDto = new PizzaItemDto();
            pizzaItemDto.setPizzaId(pizzaId);
            pizzaItemDto.setQuantity(quantity);

            // Trouver tous les ingrédients supplémentaires pour cette pizza
            List<ExtraIngredientDto> extraIngredients = new ArrayList<>();

            // Parcourir les ingrédients supplémentaires (format clé: "pizzaId:ingredientId")
            for (Map.Entry<String, Integer> extraEntry : cart.getExtraIngredients().entrySet()) {
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

    // Note: Pas besoin de méthode toEntity car nous créons/modifions des carts
    // directement dans le service à partir des informations des DTOs
}