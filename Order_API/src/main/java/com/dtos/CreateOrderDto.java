package com.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Date;

@Data
public class CreateOrderDto {
    @NotEmpty(message = "La méthode de paiement est obligatoire")
    private String paymentMethod;

    // Date et heure de retrait souhaitée
    private Date pickupTime;

    // Notes ou instructions spéciales
    private String notes;
}