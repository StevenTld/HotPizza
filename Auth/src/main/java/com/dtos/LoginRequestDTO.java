package com.dtos;

import lombok.Data;

/**
 * DTO pour la demande de connexion
 */
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}