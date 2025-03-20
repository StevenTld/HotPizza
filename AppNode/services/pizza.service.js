// services/pizza.service.js - Service pour l'API Pizza
const axios = require('axios');

// URL de base de l'API Pizza
const PIZZA_API_URL = process.env.PIZZA_API_URL || 'http://localhost:8002';

/**
 * Récupère toutes les pizzas
 */
const getAllPizzas = async () => {
    try {
        const response = await axios.get(`${PIZZA_API_URL}/api/pizza`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Récupère une pizza par son ID
 */
const getPizzaById = async (id) => {
    try {
        const response = await axios.get(`${PIZZA_API_URL}/api/pizza/${id}`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Crée une nouvelle pizza
 */
const createPizza = async (pizzaData) => {
    try {
        const response = await axios.post(`${PIZZA_API_URL}/api/pizza`, pizzaData);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Met à jour une pizza existante
 */
const updatePizza = async (id, pizzaData) => {
    try {
        const response = await axios.put(`${PIZZA_API_URL}/api/pizza/${id}`, pizzaData);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Supprime une pizza
 */
const deletePizza = async (id) => {
    try {
        const response = await axios.delete(`${PIZZA_API_URL}/api/pizza/${id}`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Gestion des erreurs de l'API
 */
const handleApiError = (error) => {
    if (error.response) {
        const customError = new Error(
            error.response.data.message || 'Erreur d\'API'
        );
        customError.statusCode = error.response.status;
        throw customError;
    }
    throw new Error('Erreur de connexion à l\'API Pizza');
};

module.exports = {
    getAllPizzas,
    getPizzaById,
    createPizza,
    updatePizza,
    deletePizza
};