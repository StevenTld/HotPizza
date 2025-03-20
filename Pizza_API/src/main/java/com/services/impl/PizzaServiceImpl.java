package com.services.impl;

import com.dtos.PizzaDto;
import com.mappers.PizzaMapper;
import com.repositories.PizzaRepository;
import com.services.PizzaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("IngredientService")
@Transactional
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;
    private final PizzaMapper pizzaMapper;

    public PizzaServiceImpl(PizzaRepository pizzaRepository, PizzaMapper pizzaMapper) {
        this.pizzaRepository = pizzaRepository;
        this.pizzaMapper = pizzaMapper;
    }

    @Override
    public PizzaDto savePizza(PizzaDto pizzaDto) {
        var pizza = pizzaMapper.toEntity(pizzaDto);
        var savedPizza = pizzaRepository.save(pizza);
        return pizzaMapper.toDto(savedPizza);
    }

    @Override
    @Transactional(readOnly = true)
    public PizzaDto getPizzaById(Long id) {
        var pizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("L'ingrédient avec l'ID %d n'existe pas", id)));
        return pizzaMapper.toDto(pizza);
    }

    @Override
    public boolean deletePizza(Long id) {
        pizzaRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PizzaDto> getAllPizza() {
        return pizzaRepository.findAll().stream()
                .map(pizzaMapper::toDto)
                .collect(Collectors.toList());
    }
}
