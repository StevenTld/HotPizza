package com.controllers;

import com.dtos.PizzaDto;
import com.services.impl.PizzaServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizza")
@CrossOrigin(origins = "http://localhost:8079", allowCredentials = "true")
public class PizzaController {

    private final PizzaServiceImpl pizzaService;

    public PizzaController(PizzaServiceImpl pizzaService) { this.pizzaService = pizzaService; }

    @GetMapping
    public List<PizzaDto> getPizzas() {
        return pizzaService.getAllPizza();
    }

    @GetMapping("/{id}")
    public PizzaDto getPizzaById(@PathVariable Long id) {
        return pizzaService.getPizzaById(id);
    }

    @PostMapping
    public PizzaDto createPizza(@RequestBody PizzaDto pizzaDto) {
        return pizzaService.savePizza(pizzaDto);
    }

    @PutMapping("/{id}")
    public PizzaDto updatePizza(@PathVariable Long id, @RequestBody PizzaDto pizzaDto) {
        return pizzaService.savePizza(pizzaDto);
    }

    @DeleteMapping("/{id}")
    public Boolean deletePizzaById(@PathVariable Long id) {
        return pizzaService.deletePizza(id);
    }
}
