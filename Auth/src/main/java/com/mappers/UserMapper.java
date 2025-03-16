package com.mappers;

import com.dtos.UserDTO;
import com.entities.User;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre User et UserDTO.
 */
@Component
public class UserMapper {

    // Convertit une entité User en DTO
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        // Le mot de passe n'est pas inclus dans le DTO pour des raisons de sécurité
        return dto;
    }

    // Convertit un DTO en entité User
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setEmail(dto.getEmail());
        // Définir le mot de passe uniquement s'il est présent dans le DTO
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }

        return user;
    }
}