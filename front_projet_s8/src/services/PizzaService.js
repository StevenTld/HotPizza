// src/services/PizzaService.js

import axios from 'axios'
import AuthService from "@/services/AuthService";

const API_URL = 'http://localhost:3000/api/pizza'

class PizzaService {
    // Récupérer toutes les pizzas
    async getAllPizzas() {
        try {
            const response = await axios.get(API_URL)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la récupération des pizzas:', error)
            throw error
        }
    }

    // Récupérer une pizza par son ID
    async getPizzaById(id) {
        try {
            const response = await axios.get(`${API_URL}/${id}`)
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la récupération de la pizza avec l'ID ${id}:`, error)
            throw error
        }
    }

    // Créer une nouvelle pizza
    async createPizza(pizzaData) {
        try {
            const response = await axios.post(API_URL, pizzaData)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la création de la pizza:', error)
            throw error
        }
    }

    // Mettre à jour une pizza existante
    async updatePizza(id, pizzaData) {
        try {
            const response = await axios.put(`${API_URL}/${id}`, pizzaData)
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la mise à jour de la pizza avec l'ID ${id}:`, error)
            throw error
        }
    }

    // Supprimer une pizza
    async deletePizza(id) {
        try {
            const response = await axios.delete(`${API_URL}/${id}`)
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la suppression de la pizza avec l'ID ${id}:`, error)
            throw error
        }
    }

    // Nouvelle méthode pour récupérer les commentaires d'une pizza
    async getPizzaComments(pizzaId) {
        try {
            const response = await axios.get(`${API_URL}/pizza/${pizzaId}`, {
                headers: {
                    'Authorization': `Bearer ${AuthService.getToken()}`
                }
            })
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la récupération des commentaires pour la pizza ${pizzaId}:`, error)
            throw error
        }
    }

    // Nouvelle méthode pour récupérer la note moyenne d'une pizza
    async getPizzaAverageRating(pizzaId) {
        try {
            const response = await axios.get(`${API_URL}/pizza/${pizzaId}/rating`, {
                headers: {
                    'Authorization': `Bearer ${AuthService.getToken()}`
                }
            })
            return response.data
        } catch (error) {
            console.error(`Erreur lors de la récupération de la note moyenne pour la pizza ${pizzaId}:`, error)
            throw error
        }
    }

    // Nouvelle méthode pour ajouter un commentaire
    async addPizzaComment(pizzaId, commentData) {
        try {
            const response = await axios.post(API_URL, {
                pizzaId: pizzaId,
                content: commentData.content,
                rating: commentData.rating
            }, {
                headers: {
                    'Authorization': `Bearer ${AuthService.getToken()}`
                }
            })
            return response.data
        } catch (error) {
            console.error(`Erreur lors de l'ajout d'un commentaire pour la pizza ${pizzaId}:`, error)
            throw error
        }
    }
}

export default new PizzaService()