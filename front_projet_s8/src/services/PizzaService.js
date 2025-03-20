// src/services/PizzaService.js

import axios from 'axios'

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
}

export default new PizzaService()