package com.services.impl;

import com.dtos.DogDto;
import com.entities.Dog;
import com.repositories.DogRepository;
import com.services.DogService;
import com.mappers.DogMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implémentation des opérations métier pour la gestion des chiens.
 * Cette classe suit le principe de Single Responsibility (SOLID).
 */
@Service("dogService")
@Transactional
public class DogServiceImpl implements DogService {

	private final DogRepository dogRepository;
	private final DogMapper dogMapper;

	/**
	 * Constructeur avec injection des dépendances
	 * L'injection par constructeur est préférée à @Autowired car :
	 * - Elle rend les dépendances obligatoires
	 * - Elle facilite les tests unitaires
	 * - Elle permet l'immutabilité
	 */
	public DogServiceImpl(DogRepository dogRepository, DogMapper dogMapper) {
		this.dogRepository = dogRepository;
		this.dogMapper = dogMapper;
	}

	/**
	 * {@inheritDoc}
	 * Cette méthode est transactionnelle par défaut grâce à @Transactional sur la classe
	 */
	@Override
	public DogDto saveDog(DogDto dogDto) {
		var dog = dogMapper.toEntity(dogDto);
		var savedDog = dogRepository.save(dog);
		return dogMapper.toDto(savedDog);
	}

	/**
	 * {@inheritDoc}
	 * Utilisation de la méthode orElseThrow pour une gestion élégante des cas d'erreur
	 */
	@Override
	@Transactional(readOnly = true)
	public DogDto getDogById(Long dogId) {
		var dog = dogRepository.findById(dogId)
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("Le chien avec l'ID %d n'existe pas", dogId)));
		return dogMapper.toDto(dog);
	}

	/**
	 * {@inheritDoc}
	 * La méthode deleteById ne lève pas d'exception si l'entité n'existe pas
	 */
	@Override
	public boolean deleteDog(Long dogId) {
		dogRepository.deleteById(dogId);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * Utilisation de l'API Stream pour une transformation fonctionnelle des données
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DogDto> getAllDogs() {
		return dogRepository.findAll().stream()
				.map(dogMapper::toDto)
				.toList();
	}
}
