// services/auth.service.js - Service pour l'API d'authentification
const axios = require('axios');

// URL de base de l'API d'authentification
const AUTH_API_URL = process.env.AUTH_API_URL || 'http://localhost:8080';

/**
 * Vérifie les identifiants utilisateur auprès de l'API Auth
 */
const verifyCredentials = async (loginData) => {
    try {
        const response = await axios.post(`${AUTH_API_URL}/api/auth/login`, loginData);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Enregistre un nouvel utilisateur
 */
const registerUser = async (userData) => {
    try {
        const response = await axios.post(`${AUTH_API_URL}/api/auth/register`, userData);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Gère les erreurs d'API
 */
const handleApiError = (error) => {
    if (error.response) {
        const customError = new Error(
            error.response.data.message || 'Erreur d\'API'
        );
        customError.statusCode = error.response.status;
        throw customError;
    }
    throw new Error('Erreur de connexion au serveur d\'authentification');
};

module.exports = {
    verifyCredentials,
    registerUser
};