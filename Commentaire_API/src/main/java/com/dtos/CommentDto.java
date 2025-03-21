package com.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class CommentDto {
    private Long id;

    private Long userId;

    @NotNull(message = "L'ID de la pizza est obligatoire")
    private Long pizzaId;

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    private Integer rating;

    private String userName;

    private Date createdAt;

    private Date updatedAt;
}