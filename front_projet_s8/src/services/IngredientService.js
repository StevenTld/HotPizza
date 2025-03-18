import axios from 'axios'

const API_URL='http://localhost:8000/api/ingredients'

class IngredientService {

    async getAllIngredients(){
        const response = await axios.get(API_URL)
        return response.data
    }


    async createIngredient(currentIngredient) {
        try {
            const response = await axios.post(API_URL, currentIngredient);
            return response.data;
        } catch (error) {
            console.error("Erreur lors de la création de l'ingrédient :", error);
            throw error; // Ou return null si tu veux éviter une erreur bloquante
        }
    }

    async updateIngredient(id, ingredient) {
        const response = await axios.put(`${API_URL}/${id}`, ingredient)
        return response.data
    }

    async deleteIngredient(id) {
        const response = await axios.delete(`${API_URL}/${id}`)
        return response.data
    }
}

export default new IngredientService()