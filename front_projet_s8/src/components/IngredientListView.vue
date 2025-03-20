<template>
  <div class="list-ingredient">
    <div class="ingredient-card" v-for="ingredient in ingredients" :key="ingredient.id">
      <div class="ingredient-image">
        <img v-if="ingredient.photo" :src="(ingredient.photo)" :alt="ingredient.name">
        <div v-else class="no-image">
          <span>Image non disponible</span>
        </div>
      </div>
      <div class="ingredient-details">
        <h3 class="ingredient-name">{{ ingredient.name }}</h3>
        <p class="ingredient-description">{{ ingredient.description }}</p>
        <div class="ingredient-price">{{ ingredient.prix }} €</div>
      </div>
    </div>
  </div>
</template>

<script>
import IngredientService from '@/services/IngredientService'

export default {
  name: 'IngredientListView',
  data() {
    return {
      ingredients: [],
    }
  },
  methods: {
    getEmptyIngredient() {
      return{
        name: "",
        description: "",
        prix: "",
        photo: ""
      }
    },
    async fetchIngredient() {
      try{
        this.ingredients = await IngredientService.getAllIngredients()
      }catch (error) {
        alert('Erreur lors de la récupération des ingrédients')
      }
    }
  },
  mounted() {
    this.fetchIngredient()
  }
}
</script>

<style scoped>
.list-ingredient {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  justify-content: center; /* Centre les colonnes quand elles ne remplissent pas toute la largeur */
}
.ingredient-card {
  width: 280px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: white;
  margin: 16px;
}

.ingredient-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.ingredient-image {
  height: 180px;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ingredient-image img {
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

.ingredient-details {
  padding: 16px;
}

.ingredient-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.ingredient-description {
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

.ingredient-price {
  font-weight: bold;
  font-size: 18px;
  color: #e74c3c;
  margin-bottom: 12px;
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
  background-color: #3498db;
  color: white;
}

.btn-add:hover {
  background-color: #2980b9;
}

.btn-details {
  background-color: #f5f5f5;
  color: #333;
}

.btn-details:hover {
  background-color: #e0e0e0;
}
</style>