package com.controllers;

import com.dtos.DogDto;
import org.springframework.web.bind.annotation.*;

import com.services.impl.DogServiceImpl;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // Port par défaut de Vue
@RestController
@RequestMapping("/api/dogs")
public class DogController {
	
	private final DogServiceImpl dogService;

	public DogController(DogServiceImpl dogService) {
		this.dogService = dogService;
	}

	/**
	 * <p>Get all dogs in the system</p>
	 * @return List<DogDto>
	 */
	@GetMapping
	public List<DogDto> getDogs() {
		return dogService.getAllDogs();
	}

	/**
	 * Method to get the dog based on the ID
	 */
	@GetMapping("/{id}")
	public DogDto getDog(@PathVariable Long id){
		return dogService.getDogById(id);
	}

	/**
	 * Create a new Dog in the system
	 */
	@PostMapping
	public DogDto saveDog(final @RequestBody DogDto dogDto){
		return dogService.saveDog(dogDto);
	}

	/**
	 * Delete a dog by it's id
	 */
	@DeleteMapping("/{id}")
	public Boolean deleteDog(@PathVariable Long id){
		return dogService.deleteDog(id);
	}
	
}
