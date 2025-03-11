package com.mappers;

import com.dtos.DogDto;
import com.entities.Dog;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * Mapper responsable de la conversion entre les entités Dog et les DTOs DogDto.
 * Un mapper permet de séparer la couche de persistance de la couche de présentation.
 * 
 * Points clés du pattern Mapper :
 * - Conversion bidirectionnelle entre DTO et Entity
 * - Gestion des null-safety
 * - Pas de logique métier, uniquement de la transformation
 */
@Component
public class DogMapper {

    /**
     * Convertit une entité Dog en DTO DogDto
     * Cette méthode est utilisée pour exposer les données aux clients de l'API
     * 
     * @param dog l'entité à convertir
     * @return le DTO correspondant ou null si l'entité est null
     */
    public DogDto toDto(Dog dog) {
        if (dog == null) {
            return null;
        }
        
        DogDto dogDto = new DogDto();
        dogDto.setId(dog.getId());
        dogDto.setName(dog.getName());
        dogDto.setRace(dog.getRace());
        return dogDto;
    }

    /**
     * Convertit un DTO DogDto en entité Dog
     * Cette méthode est utilisée pour persister les données reçues des clients
     * Note: La date de naissance n'est pas dans le DTO mais est présente dans l'entité
     * 
     * @param dogDto le DTO à convertir
     * @return l'entité correspondante ou null si le DTO est null
     */
    public Dog toEntity(DogDto dogDto) {
        if (dogDto == null) {
            return null;
        }

        Dog dog = new Dog();
        // On ne set l'ID que s'il existe (cas d'une mise à jour)
        if (dogDto.getId() != null) {
            dog.setId(dogDto.getId());
        }
        dog.setName(dogDto.getName());
        dog.setRace(dogDto.getRace());
        return dog;
    }
} 