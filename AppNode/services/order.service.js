// services/order.service.js - Service pour l'API Order
const axios = require('axios');

// URL de base de l'API Order
const ORDER_API_URL = process.env.ORDER_API_URL || 'http://localhost:8005';

/**
 * Récupère toutes les commandes d'un utilisateur
 */
const getUserOrders = async (userId) => {
    try {
        const response = await axios.get(`${ORDER_API_URL}/api/orders`, {
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
 * Récupère une commande par son ID
 */
const getOrderById = async (id, userId) => {
    try {
        const response = await axios.get(`${ORDER_API_URL}/api/orders/${id}`, {
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
 * Crée une nouvelle commande
 */
const createOrder = async (orderData, userId) => {
    try {
        // S'assurer que l'userId dans les données de commande correspond à l'utilisateur authentifié
        if (orderData.userId && orderData.userId.toString() !== userId.toString()) {
            throw new Error("Vous ne pouvez pas créer une commande pour un autre utilisateur");
        }
        
        // Ajouter l'userId si non présent
        const finalOrderData = { ...orderData };
        if (!finalOrderData.userId) {
            finalOrderData.userId = userId;
        }
        
        const response = await axios.post(`${ORDER_API_URL}/api/orders`, finalOrderData, {
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
 * Met à jour le statut d'une commande
 */
// Dans votre service
const updateOrderStatus = async (id, status, userId) => {
    try {
        console.log('Paramètres de la requête:', { id, status, userId }); // Log des paramètres

        const response = await axios.put(`${ORDER_API_URL}/api/orders/${id}/status`, null, {
            params: { status }, // Utiliser params pour les paramètres de requête
            headers: {
                'X-User-ID': userId
            }
        });
        return response.data;
    } catch (error) {
        console.error('Erreur détaillée axios:', error.response ? error.response.data : error.message);
        handleApiError(error);
    }
};

/**
 * Marque une commande comme payée
 */
const markOrderAsPaid = async (id, transactionId, userId) => {
    try {
        const response = await axios.put(`${ORDER_API_URL}/api/orders/${id}/payment?transactionId=${transactionId}`, {}, {
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
 * Supprime une commande
 */
const deleteOrder = async (id, userId) => {
    try {
        const response = await axios.delete(`${ORDER_API_URL}/api/orders/${id}`, {
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
    throw new Error('Erreur de connexion à l\'API Order');
};

module.exports = {
    getUserOrders,
    getOrderById,
    createOrder,
    updateOrderStatus,
    markOrderAsPaid,
    deleteOrder
};