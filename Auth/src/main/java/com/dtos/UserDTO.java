package com.dtos;

import lombok.Data;

/**
 * DTO pour le transfert des données d'un utilisateur.
 */
@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password; // Utilisé uniquement pour la création/modification
}