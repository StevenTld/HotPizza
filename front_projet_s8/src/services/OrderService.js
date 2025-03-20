// src/services/OrderService.js

import axios from 'axios'

const API_URL = 'http://localhost:3000/api/order'

class OrderService {
    // Récupérer toutes les commandes de l'utilisateur
    async getUserOrders() {
        try {
            const response = await axios.get(API_URL)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la récupération des commandes:', error)
            throw error
        }
    }

    // Récupérer une commande par son ID
    async getOrderById(id) {
        try {
            const response = await axios.get(`${API_URL}/${id}`)
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la récupération de la commande avec l'ID ${id}:`, error)
            throw error
        }
    }

    // Créer une nouvelle commande
    async createOrder(orderData) {
        try {
            const response = await axios.post(API_URL, orderData)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la création de la commande:', error)
            throw error
        }
    }

    // Mettre à jour le statut d'une commande
    async updateOrderStatus(id, status) {
        try {
            const response = await axios.put(`${API_URL}/${id}/status?status=${status}`)
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la mise à jour du statut de la commande avec l'ID ${id}:`, error)
            throw error
        }
    }

    // Marquer une commande comme payée
    async markOrderAsPaid(id, transactionId) {
        try {
            const response = await axios.put(`${API_URL}/${id}/payment?transactionId=${transactionId}`)
            return response.data
        } catch (error) {
            console.error(`Erreur lors du marquage de la commande avec l'ID ${id} comme payée:`, error)
            throw error
        }
    }

    // Supprimer une commande
    async deleteOrder(id) {
        try {
            await axios.delete(`${API_URL}/${id}`)
        } catch (error) {
            console.error(`Erreur lors de la suppression de la commande avec l'ID ${id}:`, error)
            throw error
        }
    }
}

export default new OrderService()