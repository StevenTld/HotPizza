// services/comments.service.js
const axios = require('axios');

// URL de base de l'API Comments
const COMMENTS_API_URL = process.env.COMMENTS_API_URL || 'http://localhost:8010';

/**
 * Récupère tous les commentaires d'une pizza
 */
const getCommentsByPizzaId = async (pizzaId) => {
    try {
        const response = await axios.get(`${COMMENTS_API_URL}/api/comments/pizza/${pizzaId}`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Récupère la note moyenne d'une pizza
 */
const getAverageRatingForPizza = async (pizzaId) => {
    try {
        const response = await axios.get(`${COMMENTS_API_URL}/api/comments/pizza/${pizzaId}/rating`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Récupère les commentaires d'un utilisateur
 */
const getUserComments = async (userId) => {
    try {
        const response = await axios.get(`${COMMENTS_API_URL}/api/comments/user`, {
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
 * Récupère un commentaire par son ID
 */
const getCommentById = async (id) => {
    try {
        const response = await axios.get(`${COMMENTS_API_URL}/api/comments/${id}`);
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Crée un nouveau commentaire
 */
const createComment = async (commentData) => {
    try {
        const response = await axios.post(
            `${COMMENTS_API_URL}/api/comments`,
            commentData,
            {
                headers: {
                    'X-User-ID': commentData.userId
                }
            }
        );
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Met à jour un commentaire existant
 */
const updateComment = async (id, commentData, userId, isAdmin) => {
    try {
        const response = await axios.put(
            `${COMMENTS_API_URL}/api/comments/${id}`,
            commentData,
            {
                headers: {
                    'X-User-ID': userId,
                    'X-User-Role': isAdmin ? 'ADMIN' : 'USER'
                }
            }
        );
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

/**
 * Supprime un commentaire
 */
const deleteComment = async (id, userId, isAdmin) => {
    try {
        const response = await axios.delete(
            `${COMMENTS_API_URL}/api/comments/${id}`,
            {
                headers: {
                    'X-User-ID': userId,
                    'X-User-Role': isAdmin ? 'ADMIN' : 'USER'
                }
            }
        );
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
    throw new Error('Erreur de connexion à l\'API Comments');
};

module.exports = {
    getCommentsByPizzaId,
    getAverageRatingForPizza,
    getUserComments,
    getCommentById,
    createComment,
    updateComment,
    deleteComment
};