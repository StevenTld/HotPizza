package com.dtos;
import lombok.Data;

import java.util.List;

@Data
public class ExtraIngredientDto {
    private Long ingredientId;
    private Integer quantity;
    private String name;
    private Double unitPrice;
}
