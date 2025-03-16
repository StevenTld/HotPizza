import axios from 'axios'

const API_URL='http://localhost:8000/api/ingredients'

class IngredientService {

    async getAllIngredients(){
        const response = await axios.get(API_URL)
        return response.data
    }
}

export default new IngredientService()