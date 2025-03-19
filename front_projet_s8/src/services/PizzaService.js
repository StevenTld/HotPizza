import axios from 'axios'

const API_URL='http://localhost:3000/api/pizza'

class PizzaService {

    async getAllPizzas(){
        const response = await axios.get(API_URL)
        return response.data
    }

}

export default new PizzaService()