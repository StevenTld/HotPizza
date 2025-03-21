// src/services/CommentService.js
import axios from 'axios';

const API_URL = 'http://localhost:3000/api/comments';

class CommentService {
    // Récupérer les commentaires d'une pizza
    async getCommentsByPizzaId(pizzaId) {
        try {
            const response = await axios.get(`${API_URL}/pizza/${pizzaId}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des commentaires:', error);
            throw error;
        }
    }

    // Récupérer la note moyenne d'une pizza
    async getAverageRatingForPizza(pizzaId) {
        try {
            const response = await axios.get(`${API_URL}/pizza/${pizzaId}/rating`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération de la note moyenne:', error);
            throw error;
        }
    }

    // Récupérer les commentaires de l'utilisateur connecté
    async getUserComments() {
        try {
            const response = await axios.get(`${API_URL}/user`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des commentaires utilisateur:', error);
            throw error;
        }
    }

    // Récupérer un commentaire par ID
    async getCommentById(id) {
        try {
            const response = await axios.get(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la récupération du commentaire avec l'ID ${id}:`, error);
            throw error;
        }
    }

    // Créer un nouveau commentaire
    async createComment(commentData) {
        try {
            const response = await axios.post(API_URL, commentData);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la création du commentaire:', error);
            throw error;
        }
    }

    // Mettre à jour un commentaire
    async updateComment(id, commentData) {
        try {
            const response = await axios.put(`${API_URL}/${id}`, commentData);
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la mise à jour du commentaire avec l'ID ${id}:`, error);
            throw error;
        }
    }

    // Supprimer un commentaire
    async deleteComment(id) {
        try {
            const response = await axios.delete(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la suppression du commentaire avec l'ID ${id}:`, error);
            throw error;
        }
    }
}

export default new CommentService();