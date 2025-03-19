package com.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemDto {
    @NotNull(message = "L'ID de l'ingrédient est obligatoire")
    private Long ingredientId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité minimum est 1")
    private Integer quantity = 1;
}