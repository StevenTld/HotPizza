package com.dtos;

import lombok.Data;

/**
 * DTO pour la réponse après connexion
 */
@Data
public class LoginResponseDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Long studentNumber;
    private Long academicYearId;
    private String formationName;
}