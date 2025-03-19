package com.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemDto {
    @NotNull(message = "L'ID de la pizza est obligatoire")
    private Long pizzaId;

    @NotBlank(message = "Le nom de la pizza est obligatoire")
    private String pizzaName;

    @NotNull(message = "Le prix unitaire est obligatoire")
    private Double unitPrice;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité minimum est 1")
    private Integer quantity = 1;
}