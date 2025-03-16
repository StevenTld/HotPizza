package com.mappers;

import com.dtos.IngredientDto;
import com.entities.Ingredient;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class IngredientMapper {

    public IngredientDto toDto(Ingredient ingredient) {

        if(ingredient == null) {
            return null;
        }

        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setId(ingredient.getId());
        ingredientDto.setName(ingredient.getName());
        ingredientDto.setDescription(ingredient.getDescription());
        ingredientDto.setPhoto(ingredient.getPhoto());
        ingredientDto.setPrix(ingredient.getPrix());
        return ingredientDto;
    }

    public Ingredient toEntity(IngredientDto ingredientDto) {
        if(ingredientDto == null) {
            return null;
        }

        Ingredient ingredient = new Ingredient();
        if(ingredientDto.getId() != null){
            ingredient.setId(ingredientDto.getId());
        }
        ingredient.setName(ingredientDto.getName());
        ingredient.setDescription(ingredientDto.getDescription());
        ingredient.setPhoto(ingredientDto.getPhoto());
        ingredient.setPrix(ingredientDto.getPrix());
        return ingredient;
    }

}
