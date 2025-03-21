<template>
  <div class="list-ingredient-container">
    <h2 class="section-title">Nos Ingrédients</h2>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement des ingrédients...</p>
    </div>

    <div v-else-if="ingredients.length === 0" class="no-results">
      <p>Aucun ingrédient disponible.</p>
    </div>

    <div v-else class="list-ingredient">
      <div class="ingredient-card" v-for="ingredient in ingredients" :key="ingredient.id">
        <div class="ingredient-image">
          <img v-if="ingredient.photo" :src="ingredient.photo" :alt="ingredient.name">
          <div v-else class="no-image">
            <span>Image non disponible</span>
          </div>
        </div>
        <div class="ingredient-details">
          <h3 class="ingredient-name">{{ ingredient.name }}</h3>
          <p class="ingredient-description">{{ ingredient.description }}</p>
          <div class="ingredient-price">{{ formatPrice(ingredient.prix) }} €</div>
        </div>
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
      loading: true
    }
  },
  methods: {
    getEmptyIngredient() {
      return {
        name: "",
        description: "",
        prix: "",
        photo: ""
      }
    },
    async fetchIngredient() {
      try {
        this.loading = true;
        this.ingredients = await IngredientService.getAllIngredients();
      } catch (error) {
        console.error('Erreur lors de la récupération des ingrédients:', error);
      } finally {
        this.loading = false;
      }
    },
    formatPrice(price) {
      return price ? parseFloat(price).toFixed(2) : '0.00';
    }
  },
  mounted() {
    this.fetchIngredient();
  }
}
</script>

<style scoped>
.list-ingredient-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 30px;
  color: #333;
  text-align: left;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 15px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #00c3ff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #666;
}

.list-ingredient {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  justify-content: center;
}

.ingredient-card {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: white;
}

.ingredient-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.ingredient-image {
  height: 200px;
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
  font-size: 20px;
  color: #333;
  font-weight: 600;
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
  font-size: 22px;
  color: #e74c3c;
  margin-bottom: 16px;
}
</style>