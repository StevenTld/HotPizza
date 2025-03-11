package com.services;

import com.dtos.DogDto;

import java.util.List;

/**
 * Interface définissant les opérations métier disponibles pour la gestion des chiens.
 * Cette interface suit le principe d'Interface Segregation (SOLID).
 */
public interface DogService {
    /**
     * Sauvegarde un chien dans le système
     * @param dogDto les données du chien à sauvegarder
     * @return le chien sauvegardé avec son ID généré
     */
    DogDto saveDog(DogDto dogDto);

    /**
     * Récupère un chien par son identifiant
     * @param dogId l'identifiant du chien recherché
     * @return le chien trouvé
     * @throws jakarta.persistence.EntityNotFoundException si le chien n'existe pas
     */
    DogDto getDogById(Long dogId);

    /**
     * Supprime un chien du système
     * @param dogId l'identifiant du chien à supprimer
     * @return true si la suppression a réussi
     */
    boolean deleteDog(Long dogId);

    /**
     * Récupère tous les chiens du système
     * @return la liste des chiens
     */
    List<DogDto> getAllDogs();
}
