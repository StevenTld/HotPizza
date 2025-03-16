package com.services;

import com.dtos.IngredientDto;

import java.util.List;

public interface IngredientService {

    IngredientDto saveIngredient(IngredientDto ingredientDto);

    IngredientDto getIngredientById(Long id);

    boolean deleteIngredient(Long id);

    List<IngredientDto> getAllIngredients();
}
