// services/auth.service.js - Service pour l'API d'authentification
const axios = require('axios');

// URL de base de l'API d'authentification
const AUTH_API_URL = process.env.AUTH_API_URL || 'http://localhost:8080';

console.log('AUTH_API_URL configurée :', AUTH_API_URL);

/**
 * Vérifie les identifiants utilisateur auprès de l'API Auth
 */
const verifyCredentials = async (loginData) => {
    try {
        console.log('Tentative de connexion avec les données :', JSON.stringify(loginData));
        console.log('URL complète de connexion :', `${AUTH_API_URL}/api/auth/login`);

        const response = await axios.post(`${AUTH_API_URL}/api/auth/login`, loginData);
        console.log('Réponse de connexion réussie :', JSON.stringify(response.data));
        return response.data;
    } catch (error) {
        console.error('Erreur lors de la vérification des identifiants :');
        console.error('Message :', error.message);
        if (error.response) {
            console.error('Status :', error.response.status);
            console.error('Données de réponse :', JSON.stringify(error.response.data));
        } else if (error.request) {
            console.error('Aucune réponse reçue - Problème de connexion au serveur');
            console.error('Requête :', error.request);
        }
        handleApiError(error);
    }
};

/**
 * Enregistre un nouvel utilisateur
 */
const registerUser = async (userData) => {
    try {
        console.log("Tentative d'inscription avec les données:", userData);
        console.log("URL d'inscription complète:", `${AUTH_API_URL}/api/auth/register`);

        // Ajouter des en-têtes pour le débogage
        const config = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        console.log("Configuration de la requête:", config);

        const response = await axios.post(`${AUTH_API_URL}/api/auth/register`, userData, config);
        console.log("Réponse complète:", response);
        console.log("Données de réponse:", response.data);
        return response.data;
    } catch (error) {
        console.error("Erreur complète lors de l'inscription:", error);

        if (error.response) {
            console.error("Détails de la réponse d'erreur:");
            console.error("Status:", error.response.status);
            console.error("Headers:", error.response.headers);
            console.error("Data:", error.response.data);
        } else if (error.request) {
            console.error("Pas de réponse reçue. Requête:", error.request);
        } else {
            console.error("Erreur de configuration:", error.message);
        }

        handleApiError(error);
    }
};

/**
 * Gère les erreurs d'API
 */
const handleApiError = (error) => {
    console.error('Gestion de l\'erreur API dans handleApiError');

    if (error.response) {
        console.error('Erreur avec réponse du serveur :', error.response.status);
        console.error('Message d\'erreur :', error.response.data.message || 'Pas de message spécifique');

        const customError = new Error(
            error.response.data.message || 'Erreur d\'API'
        );
        customError.statusCode = error.response.status;
        throw customError;
    }

    console.error('Erreur sans réponse du serveur');
    throw new Error('Erreur de connexion au serveur d\'authentification');
};

module.exports = {
    verifyCredentials,
    registerUser
};