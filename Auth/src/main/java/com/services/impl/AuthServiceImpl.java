package com.services.impl;

import com.dtos.LoginRequestDTO;
import com.dtos.LoginResponseDTO;
import com.dtos.UserDTO;

import com.entities.User;
import com.mappers.UserMapper;
import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification amélioré avec cryptage des mots de passe
 */
@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authentification d'un utilisateur - utilisé par le serveur Node.js
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Chercher l'utilisateur par email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        // Vérifier le mot de passe avec BCrypt
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // Créer la réponse
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        return response;
    }

    /**
     * Enregistre un nouvel utilisateur avec cryptage du mot de passe
     */
    @Transactional
    public UserDTO register(UserDTO userDTO) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Utiliser le mapper pour convertir le DTO en entité
        User user = userMapper.toEntity(userDTO);

        // Crypter le mot de passe avant de l'enregistrer
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Utiliser le mapper pour convertir l'entité sauvegardée en DTO
        return userMapper.toDTO(savedUser);
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}