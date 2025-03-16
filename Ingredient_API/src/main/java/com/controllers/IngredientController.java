package com.controllers;

import com.services.IngredientService;
import org.springframework.web.bind.annotation.*;

import com.services.impl.IngredientServiceImpl;
import com.dtos.IngredientDto;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:8081")
public class IngredientController {

    private final IngredientServiceImpl ingredientService;

    public IngredientController(IngredientServiceImpl ingredientService) { this.ingredientService = ingredientService; }

    @GetMapping
    public List<IngredientDto> getIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public IngredientDto getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id);
    }

    @PostMapping
    public IngredientDto createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ingredientService.saveIngredient(ingredientDto);
    }

    @DeleteMapping("/id")
    public Boolean deleteIngredientById(@PathVariable Long id) {
        return ingredientService.deleteIngredient(id);
    }
}
