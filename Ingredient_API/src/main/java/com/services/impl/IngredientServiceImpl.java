package com.services.impl;

import com.dtos.IngredientDto;
import com.mappers.IngredientMapper;
import com.repositories.IngredientRepository;
import com.services.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("IngredientService")
@Transactional
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public IngredientDto saveIngredient(IngredientDto ingredientDto) {
        var ingredient = ingredientMapper.toEntity(ingredientDto);
        var savedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientDto getIngredientById(Long id) {
        var ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("L'ingrédient avec l'ID %d n'existe pas", id)));
        return ingredientMapper.toDto(ingredient);
    }

    @Override
    public boolean deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }
}
