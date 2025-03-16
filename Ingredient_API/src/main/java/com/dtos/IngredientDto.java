package com.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientDto {

    @NotNull
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "Le prix est obligatoire")
    private Double prix;

    private String photo;
}
