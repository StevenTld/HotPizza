const axios = require('axios');

// URL de base de l'API Commentaires
const COMMENT_API_URL = process.env.COMMENT_API_URL || 'http://localhost:8003';

/**
 * Gestion des erreurs de l'API
 */
const handleApiError = (error) => {
    if (error.response) {
        const customError = new Error(
            error.response.data.message || 'Erreur d\'API de commentaires'
        );
        customError.statusCode = error.response.status;
        throw customError;
    }
    throw new Error('Erreur de connexion à l\'API de commentaires');
};

/**
 * Ajouter un commentaire
 */
const addComment = async (commentData, userId) => {
    try {
        // Vérifier et ajouter l'userId si nécessaire
        const finalCommentData = { ...commentData };
        if (!finalCommentData.userId) {
            finalCommentData.userId = userId;
        }

        const response = await axios.post(`${COMMENT_API_URL}/api/comments`, finalCommentData, {
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
 * Récupérer les commentaires d'une pizza
 */
const getCommentsByPizzaId = async (pizzaId, userId) => {
    try {
        const response = await axios.get(`${COMMENT_API_URL}/api/comments/pizza/${pizzaId}`, {
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
 * Récupérer la note moyenne d'une pizza
 */
const getPizzaAverageRating = async (pizzaId, userId) => {
    try {
        const response = await axios.get(`${COMMENT_API_URL}/api/comments/pizza/${pizzaId}/rating`, {
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
 * Mettre à jour un commentaire
 */
const updateComment = async (commentId, commentData, userId) => {
    try {
        const response = await axios.put(`${COMMENT_API_URL}/api/comments/${commentId}`, commentData, {
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
 * Supprimer un commentaire
 */
const deleteComment = async (commentId, userId) => {
    try {
        const response = await axios.delete(`${COMMENT_API_URL}/api/comments/${commentId}`, {
            headers: {
                'X-User-ID': userId
            }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

module.exports = {
    addComment,
    getCommentsByPizzaId,
    getPizzaAverageRating,
    updateComment,
    deleteComment
};