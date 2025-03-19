// services/ingredients.service.js - Service pour l'API Ingredients
const axios = require('axios');

// URL de base de l'API Ingredients
const PIZZA_API_URL = process.env.PIZZA_API_URL || 'http://localhost:8002';

/**
 * Récupère tous les ingrédients
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
 * Récupère un ingrédient par son ID
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
 * Crée un nouvel ingrédient
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
 * Supprime un ingrédient
 */
const deleteIngredient = async (id) => {
    try {
        const response = await axios.delete(`${INGREDIENT_API_URL}/api/ingredients/${id}`);
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
    throw new Error('Erreur de connexion à l\'API Ingredients');
};

module.exports = {
    getAllPizzas,
    //getIngredientById,
    //createIngredient,
    //deleteIngredient
};