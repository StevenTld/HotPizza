// services/ingredients.service.js - Service pour l'API Ingredients
const axios = require('axios');

// URL de base de l'API Ingredients
const INGREDIENT_API_URL = process.env.INGREDIENT_API_URL || 'http://localhost:8000';

/**
 * Récupère tous les ingrédients
 */
const getAllIngredients = async () => {
    try {
        const response = await axios.get(`${INGREDIENT_API_URL}/api/ingredients`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Récupère un ingrédient par son ID
 */
const getIngredientById = async (id) => {
    try {
        const response = await axios.get(`${INGREDIENT_API_URL}/api/ingredients/${id}`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Crée un nouvel ingrédient
 */
const createIngredient = async (ingredientData) => {
    try {
        const response = await axios.post(`${INGREDIENT_API_URL}/api/ingredients`, ingredientData);
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
    getAllIngredients,
    getIngredientById,
    createIngredient,
    deleteIngredient
};