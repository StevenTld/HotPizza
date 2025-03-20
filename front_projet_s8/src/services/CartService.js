// src/services/CartService.js

import axios from 'axios'

const API_URL = 'http://localhost:3000/api/cart'

class CartService {
    // Récupérer le panier de l'utilisateur
    async getUserCart() {
        try {
            const response = await axios.get(API_URL)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la récupération du panier:', error)
            throw error
        }
    }

    // Ajouter une pizza au panier
    async addPizzaToCart(pizzaId, quantity = 1) {
        try {
            const response = await axios.post(`${API_URL}/pizzas?pizzaId=${pizzaId}&quantity=${quantity}`)
            return response.data
        } catch (error) {
            console.error('Erreur lors de l\'ajout de la pizza au panier:', error)
            throw error
        }
    }

    // Ajouter un ingrédient supplémentaire à une pizza
    async addExtraIngredientToCart(pizzaId, ingredientId, quantity = 1) {
        try {
            const response = await axios.post(
                `${API_URL}/pizzas/${pizzaId}/extras?ingredientId=${ingredientId}&quantity=${quantity}`
            )
            return response.data
        } catch (error) {
            console.error('Erreur lors de l\'ajout de l\'ingrédient supplémentaire:', error)
            throw error
        }
    }

    // Supprimer une pizza du panier
    async removePizzaFromCart(pizzaId) {
        try {
            const response = await axios.delete(`${API_URL}/pizzas/${pizzaId}`)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la suppression de la pizza du panier:', error)
            throw error
        }
    }

    // Supprimer un ingrédient supplémentaire d'une pizza
    async removeExtraIngredientFromCart(pizzaId, ingredientId) {
        try {
            const response = await axios.delete(`${API_URL}/pizzas/${pizzaId}/extras/${ingredientId}`)
            return response.data
        } catch (error) {
            console.error('Erreur lors de la suppression de l\'ingrédient supplémentaire:', error)
            throw error
        }
    }

    // Vider le panier
    async clearCart() {
        try {
            const response = await axios.delete(`${API_URL}/clear`)
            return response.data
        } catch (error) {
            console.error('Erreur lors du vidage du panier:', error)
            throw error
        }
    }
}

export default new CartService()