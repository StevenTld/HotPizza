<template>
  <div class="list-pizza">
    <div class="pizza-card" v-for="pizza in pizzas" :key="pizza.id">
      <div class="pizza-image">
        <img v-if="pizza.photo" :src="(pizza.photo)" :alt="pizza.name">
        <div v-else class="no-image">
          <span>Image non disponible</span>
        </div>
      </div>
      <div class="pizza-details">
        <h3 class="pizza-name">{{ pizza.name }}</h3>
        <p class="pizza-composition">{{ pizza.composition }}</p>
        <div class="pizza-price">{{ pizza.prix }} €</div>
        <div class="card-actions">
          <button class="btn-add">Ajouter au panier</button>
          <button class="btn-details">Détails</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PizzaService from '@/services/PizzaService'

export default {
  name: 'PizzaListView',
  data() {
    return {
      pizzas: [],
    }
  },
  methods: {
    getEmptyPizza() {
      return{
        name: "",
        composition: "",
        prix: "",
        photo: ""
      }
    },
    async fetchPizzas() {
      try{
        this.pizzas = await PizzaService.getAllPizzas()
      }catch (error) {
        alert('Erreur lors de la récupération des pizzas 65')
      }
    }
  },
  mounted() {
    this.fetchPizzas()
  }
}
</script>

<style scoped>
.list-pizza {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  justify-content: center;
}

.pizza-card {
  width: 300px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: white;
  margin: 16px;
}

.pizza-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.pizza-image {
  height: 200px;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pizza-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  color: #999;
  font-style: italic;
}

.pizza-details {
  padding: 16px;
}

.pizza-name {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.pizza-composition {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.pizza-price {
  font-weight: bold;
  font-size: 22px;
  color: #e74c3c;
  margin-bottom: 16px;
}

.card-actions {
  display: flex;
  justify-content: space-between;
}

.btn-add, .btn-details {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-add {
  background-color: #e74c3c;
  color: white;
}

.btn-add:hover {
  background-color: #c0392b;
}

.btn-details {
  background-color: #f5f5f5;
  color: #333;
}

.btn-details:hover {
  background-color: #e0e0e0;
}
</style>