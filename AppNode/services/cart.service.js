// services/cart.service.js - Service pour l'API Cart
const axios = require('axios');

// URL de base de l'API Cart (même microservice que Order)
const CART_API_URL = process.env.CART_API_URL || 'http://localhost:8081';

const getUserCart = async (userId) => {
    console.log("Dans cartService.getUserCart - userId reçu:", userId);
    try {
        // Log avant la requête axios
        console.log(`Appel de ${CART_API_URL}/api/cart avec X-User-ID:`, userId);
        
        const response = await axios.get(`${CART_API_URL}/api/cart`, {
            headers: {
                'X-User-ID': userId
            }
        });
        
        // Log après la requête axios
        console.log("Réponse du microservice:", response.status);
        console.log("Données reçues du microservice:", JSON.stringify(response.data));
        return response.data;
    } catch (error) {
        console.error("Erreur dans getUserCart:", error.message);
        // Si c'est une erreur réseau
        if (error.request && !error.response) {
            console.error("Erreur réseau - le microservice est-il accessible?");
        }
        // Si c'est une erreur du serveur
        else if (error.response) {
            console.error("Erreur du serveur:", error.response.status, error.response.data);
        }
        handleApiError(error);
    }
};

/**
 * Ajoute une pizza au panier
 */
const addPizzaToCart = async (pizzaId, quantity, userId) => {
    try {
        const response = await axios.post(`${CART_API_URL}/api/cart/pizzas?pizzaId=${pizzaId}&quantity=${quantity || 1}`, {}, {
            headers: {
                'X-User-ID': userId
            }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Ajoute un ingrédient supplémentaire à une pizza
 */
const addExtraIngredientToCart = async (pizzaId, ingredientId, quantity, userId) => {
    try {
        const response = await axios.post(
            `${CART_API_URL}/api/cart/pizzas/${pizzaId}/extras?ingredientId=${ingredientId}&quantity=${quantity || 1}`,
            {},
            {
                headers: {
                    'X-User-ID': userId
                }
            }
        );
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Supprime une pizza du panier
 */
const removePizzaFromCart = async (pizzaId, userId) => {
    try {
        const response = await axios.delete(`${CART_API_URL}/api/cart/pizzas/${pizzaId}`, {
            headers: {
                'X-User-ID': userId
            }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Supprime un ingrédient supplémentaire d'une pizza
 */
const removeExtraIngredientFromCart = async (pizzaId, ingredientId, userId) => {
    try {
        const response = await axios.delete(`${CART_API_URL}/api/cart/pizzas/${pizzaId}/extras/${ingredientId}`, {
            headers: {
                'X-User-ID': userId
            }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Vide le panier
 */
const clearCart = async (userId) => {
    try {
        const response = await axios.delete(`${CART_API_URL}/api/cart/clear`, {
            headers: {
                'X-User-ID': userId
            }
        });
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
    throw new Error('Erreur de connexion à l\'API Cart');
};

module.exports = {
    getUserCart,
    addPizzaToCart,
    addExtraIngredientToCart,
    removePizzaFromCart,
    removeExtraIngredientFromCart,
    clearCart
};