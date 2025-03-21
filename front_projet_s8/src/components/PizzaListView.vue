<template>
  <div class="list-pizza-container">
    <h2 class="section-title">Nos Pizzas</h2>

    <div class="filters">
      <div class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="Rechercher une pizza..." />
      </div>
      <div class="sort-options">
        <label for="sort">Trier par :</label>
        <select id="sort" v-model="sortOption">
          <option value="name-asc">Nom (A-Z)</option>
          <option value="name-desc">Nom (Z-A)</option>
          <option value="price-asc">Prix (croissant)</option>
          <option value="price-desc">Prix (décroissant)</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement des pizzas...</p>
    </div>

    <div v-else-if="filteredPizzas.length === 0" class="no-results">
      <p>Aucune pizza trouvée pour votre recherche.</p>
    </div>

    <div v-else class="list-pizza">
      <div class="pizza-card" v-for="pizza in filteredPizzas" :key="pizza.id">
        <div class="pizza-image">
          <img v-if="pizza.photo" :src="pizza.photo" :alt="pizza.name">
          <div v-else class="no-image">
            <span>Image non disponible</span>
          </div>
        </div>
        <div class="pizza-details">
          <h3 class="pizza-name">{{ pizza.name }}</h3>
          <p class="pizza-composition">{{ pizza.composition }}</p>
          <div class="pizza-price">{{ formatPrice(pizza.prix) }} €</div>
          <div class="card-actions">
            <button class="btn-add" @click="addToCart(pizza)" :disabled="isAddingToCart[pizza.id]">
              {{ isAddingToCart[pizza.id] ? 'Ajout en cours...' : 'Ajouter au panier' }}
            </button>
            <button class="btn-details" @click="showPizzaDetails(pizza)">Détails</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal détails pizza -->
    <div v-if="showModal" class="pizza-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ selectedPizza.name }}</h3>
          <button class="close-modal" @click="closeModal">&times;</button>
        </div>

        <div class="modal-body">
          <div class="modal-image">
            <img v-if="selectedPizza.photo" :src="selectedPizza.photo" :alt="selectedPizza.name">
            <div v-else class="no-modal-image">
              <span>Image non disponible</span>
            </div>
          </div>

          <div class="modal-info">
            <div class="modal-description">
              <h4>Composition</h4>
              <p>{{ selectedPizza.composition }}</p>
            </div>

            <div class="modal-extras">
              <h4>Personnalisez votre pizza</h4>
              <p class="modal-extras-info">Ajoutez des ingrédients supplémentaires pour personnaliser votre pizza.</p>

              <div class="extras-list">
                <div v-for="ingredient in availableIngredients" :key="ingredient.id" class="extra-item">
                  <div class="extra-info">
                    <span class="extra-name">{{ ingredient.name }}</span>
                    <span class="extra-price">+{{ formatPrice(ingredient.prix) }} €</span>
                  </div>
                  <div class="extra-actions">
                    <button
                        class="btn-quantity"
                        @click="decreaseIngredientQuantity(ingredient)"
                        :disabled="!selectedIngredients[ingredient.id]"
                    >-</button>
                    <span class="extra-quantity">{{ selectedIngredients[ingredient.id] || 0 }}</span>
                    <button
                        class="btn-quantity"
                        @click="increaseIngredientQuantity(ingredient)"
                    >+</button>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-quantity">
              <h4>Quantité</h4>
              <div class="quantity-selector">
                <button
                    class="btn-quantity"
                    @click="decreasePizzaQuantity()"
                    :disabled="pizzaQuantity <= 1"
                >-</button>
                <span class="pizza-quantity">{{ pizzaQuantity }}</span>
                <button
                    class="btn-quantity"
                    @click="increasePizzaQuantity()"
                >+</button>
              </div>
            </div>

            <div class="modal-total">
              <div class="total-label">Total :</div>
              <div class="total-price">{{ formatPrice(calculateTotal()) }} €</div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeModal">Annuler</button>
          <button
              class="btn-add-to-cart"
              @click="addCustomPizzaToCart()"
              :disabled="isAddingCustomPizza"
          >
            {{ isAddingCustomPizza ? 'Ajout en cours...' : 'Ajouter au panier' }}
          </button>
        </div>
      </div>
    </div>
    <div class="modal-comments">
      <h4>Commentaires</h4>

      <!-- Note moyenne -->
      <div v-if="averageRating !== null" class="average-rating">
        Note moyenne :
        <span>{{ averageRating.toFixed(1) }} / 5</span>
      </div>

      <!-- Liste des commentaires -->
      <div v-if="comments.length" class="comments-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="comment-user">{{ comment.userId }}</span>
            <span class="comment-rating">{{ comment.rating }}/5</span>
          </div>
          <p class="comment-content">{{ comment.content }}</p>
          <small class="comment-date">{{ formatCommentDate(comment.createdAt) }}</small>
        </div>
      </div>
      <p v-else class="no-comments">Aucun commentaire pour cette pizza</p>

      <!-- Formulaire d'ajout de commentaire -->
      <div class="add-comment-form">
        <h5>Ajouter un commentaire</h5>
        <select v-model="newComment.rating">
          <option v-for="n in 5" :key="n" :value="n">{{ n }}/5</option>
        </select>
        <textarea
            v-model="newComment.content"
            placeholder="Votre commentaire..."
        ></textarea>
        <button @click="submitComment">Envoyer</button>
      </div>
    </div>
    <!-- Toast notification -->
    <div v-if="showToast" class="toast" :class="{ 'success': toastType === 'success', 'error': toastType === 'error' }">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script>
import PizzaService from '@/services/PizzaService';
import IngredientService from '@/services/IngredientService';
import CartService from '@/services/CartService';
import AuthService from '@/services/AuthService';
import {ref, computed, reactive, onMounted, watch} from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'PizzaListView',
  setup() {
    const router = useRouter();
    const pizzas = ref([]);
    const ingredients = ref([]);
    const loading = ref(true);
    const searchQuery = ref('');
    const sortOption = ref('name-asc');
    const isAddingToCart = reactive({});

    // Modal state
    const showModal = ref(false);
    const selectedPizza = ref({});
    const pizzaQuantity = ref(1);
    const selectedIngredients = reactive({});
    const isAddingCustomPizza = ref(false);

    // Toast notification
    const showToast = ref(false);
    const toastMessage = ref('');
    const toastType = ref('success');

    const comments = ref([])
    const averageRating = ref(null)
    const newComment = reactive({
      rating: 5,
      content: ''
    })

    // Charger les commentaires quand la pizza est sélectionnée
    const loadPizzaComments = async () => {
      try {
        comments.value = await PizzaService.getPizzaComments(selectedPizza.value.id)
        averageRating.value = await PizzaService.getPizzaAverageRating(selectedPizza.value.id)
      } catch (error) {
        console.error('Erreur lors du chargement des commentaires', error)
        displayToast('Erreur lors du chargement des commentaires', 'error')
      }
    }

    // Soumettre un nouveau commentaire
    const submitComment = async () => {
      if (!AuthService.isLoggedIn()) {
        router.push('/login')
        return
      }

      try {
        await PizzaService.addPizzaComment(selectedPizza.value.id, newComment)
        displayToast('Commentaire ajouté avec succès', 'success')

        // Réinitialiser le formulaire et recharger les commentaires
        newComment.content = ''
        newComment.rating = 5
        await loadPizzaComments()
      } catch (error) {
        console.error('Erreur lors de l\'ajout du commentaire', error)
        displayToast('Erreur lors de l\'ajout du commentaire', 'error')
      }
    }

    // Formater la date du commentaire
    const formatCommentDate = (dateString) => {
      return new Date(dateString).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      })
    }

    // Appeler loadPizzaComments quand le modal est ouvert
    watch(showModal, (newValue) => {
      if (newValue) {
        loadPizzaComments()
      }
    })
    // Fetch data
    const fetchPizzas = async () => {
      try {
        loading.value = true;
        pizzas.value = await PizzaService.getAllPizzas();
      } catch (error) {
        console.error('Erreur lors de la récupération des pizzas:', error);
        displayToast('Erreur lors du chargement des pizzas', 'error');
      } finally {
        loading.value = false;
      }
    };

    const fetchIngredients = async () => {
      try {
        ingredients.value = await IngredientService.getAllIngredients();
      } catch (error) {
        console.error('Erreur lors de la récupération des ingrédients:', error);
      }
    };

    // Computed properties
    const filteredPizzas = computed(() => {
      let result = [...pizzas.value];

      // Filtre par recherche
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(pizza =>
            pizza.name.toLowerCase().includes(query) ||
            pizza.composition.toLowerCase().includes(query)
        );
      }

      // Tri
      switch (sortOption.value) {
        case 'name-asc':
          result.sort((a, b) => a.name.localeCompare(b.name));
          break;
        case 'name-desc':
          result.sort((a, b) => b.name.localeCompare(a.name));
          break;
        case 'price-asc':
          result.sort((a, b) => a.prix - b.prix);
          break;
        case 'price-desc':
          result.sort((a, b) => b.prix - a.prix);
          break;
      }

      return result;
    });

    const availableIngredients = computed(() => {
      return ingredients.value;
    });

    // Methods
    const formatPrice = (price) => {
      return price ? parseFloat(price).toFixed(2) : '0.00';
    };

    const addToCart = async (pizza) => {
      if (!AuthService.isLoggedIn()) {
        router.push('/login');
        return;
      }

      try {
        isAddingToCart[pizza.id] = true;
        await CartService.addPizzaToCart(pizza.id, 1);
        displayToast(`${pizza.name} ajoutée au panier`, 'success');
      } catch (error) {
        console.error('Erreur lors de l\'ajout au panier:', error);
        displayToast('Erreur lors de l\'ajout au panier', 'error');
      } finally {
        isAddingToCart[pizza.id] = false;
      }
    };

    const showPizzaDetails = (pizza) => {
      selectedPizza.value = { ...pizza };
      pizzaQuantity.value = 1;

      // Réinitialiser les ingrédients sélectionnés
      Object.keys(selectedIngredients).forEach(key => {
        delete selectedIngredients[key];
      });

      showModal.value = true;
    };

    const closeModal = () => {
      showModal.value = false;
    };

    const increaseIngredientQuantity = (ingredient) => {
      if (!selectedIngredients[ingredient.id]) {
        selectedIngredients[ingredient.id] = 0;
      }
      selectedIngredients[ingredient.id]++;
    };

    const decreaseIngredientQuantity = (ingredient) => {
      if (selectedIngredients[ingredient.id] > 0) {
        selectedIngredients[ingredient.id]--;

        if (selectedIngredients[ingredient.id] === 0) {
          delete selectedIngredients[ingredient.id];
        }
      }
    };

    const increasePizzaQuantity = () => {
      pizzaQuantity.value++;
    };

    const decreasePizzaQuantity = () => {
      if (pizzaQuantity.value > 1) {
        pizzaQuantity.value--;
      }
    };

    const calculateTotal = () => {
      let total = selectedPizza.value.prix * pizzaQuantity.value;

      // Ajouter le prix des ingrédients supplémentaires
      for (const [ingredientId, quantity] of Object.entries(selectedIngredients)) {
        const ingredient = ingredients.value.find(i => i.id == ingredientId);
        if (ingredient) {
          total += ingredient.prix * quantity * pizzaQuantity.value;
        }
      }

      return total;
    };

    const addCustomPizzaToCart = async () => {
      if (!AuthService.isLoggedIn()) {
        router.push('/login');
        return;
      }

      try {
        isAddingCustomPizza.value = true;

        // Ajouter la pizza de base au panier
        await CartService.addPizzaToCart(selectedPizza.value.id, pizzaQuantity.value);

        // Ajouter les ingrédients supplémentaires
        for (const [ingredientId, quantity] of Object.entries(selectedIngredients)) {
          if (quantity > 0) {
            await CartService.addExtraIngredientToCart(
                selectedPizza.value.id,
                ingredientId,
                quantity * pizzaQuantity.value
            );
          }
        }

        displayToast(`${pizzaQuantity.value}x ${selectedPizza.value.name} ajoutée(s) au panier`, 'success');
        closeModal();
      } catch (error) {
        console.error('Erreur lors de l\'ajout au panier:', error);
        displayToast('Erreur lors de l\'ajout au panier', 'error');
      } finally {
        isAddingCustomPizza.value = false;
      }
    };

    const displayToast = (message, type = 'success') => {
      toastMessage.value = message;
      toastType.value = type;
      showToast.value = true;

      setTimeout(() => {
        showToast.value = false;
      }, 3000);
    };

    onMounted(() => {
      fetchPizzas();
      fetchIngredients();
    });

    return {
      pizzas,
      loading,
      searchQuery,
      sortOption,
      filteredPizzas,
      showModal,
      selectedPizza,
      pizzaQuantity,
      selectedIngredients,
      availableIngredients,
      isAddingToCart,
      isAddingCustomPizza,
      showToast,
      toastMessage,
      toastType,
      formatPrice,
      addToCart,
      showPizzaDetails,
      closeModal,
      increaseIngredientQuantity,
      decreaseIngredientQuantity,
      increasePizzaQuantity,
      decreasePizzaQuantity,
      calculateTotal,
      addCustomPizzaToCart,
      comments,
      averageRating,
      newComment,
      submitComment,
      formatCommentDate
    };
  }
};
</script>

<style scoped>

/* Styles pour les commentaires */
.modal-comments {
  margin-top: 20px;
}

.average-rating {
  text-align: center;
  font-size: 18px;
  margin-bottom: 15px;
}

.comments-list {
  max-height: 300px;
  overflow-y: auto;
}

.comment-item {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.comment-user {
  font-weight: bold;
}

.comment-rating {
  color: #e74c3c;
}

.comment-content {
  margin-bottom: 5px;
}

.comment-date {
  color: #666;
}

.no-comments {
  text-align: center;
  color: #999;
}

.add-comment-form {
  margin-top: 20px;
}

.add-comment-form textarea {
  width: 100%;
  min-height: 100px;
  margin-bottom: 10px;
}
.list-pizza-container {
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

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.search-bar input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.sort-options {
  display: flex;
  align-items: center;
}

.sort-options label {
  margin-right: 10px;
  font-weight: 500;
}

.sort-options select {
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #666;
}

.list-pizza {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  justify-content: center;
}

.pizza-card {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: white;
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
  transition: all 0.2s;
}

.btn-add {
  background-color: #e74c3c;
  color: white;
}

.btn-add:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-add:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.btn-details {
  background-color: #f5f5f5;
  color: #333;
}

.btn-details:hover {
  background-color: #e0e0e0;
}

/* Modal styles */
.pizza-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 800px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 22px;
  color: #333;
  margin: 0;
}

.close-modal {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.close-modal:hover {
  color: #333;
}

.modal-body {
  display: flex;
  flex-direction: column;
}

@media (min-width: 768px) {
  .modal-body {
    flex-direction: row;
  }
}

.modal-image {
  flex: 1;
  max-width: 100%;
  height: 300px;
  background-color: #f5f5f5;
  overflow: hidden;
}

@media (min-width: 768px) {
  .modal-image {
    max-width: 40%;
  }
}

.modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-modal-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-style: italic;
}

.modal-info {
  flex: 1;
  padding: 20px;
}

.modal-description {
  margin-bottom: 20px;
}

.modal-description h4,
.modal-extras h4,
.modal-quantity h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.modal-description p {
  color: #666;
  line-height: 1.5;
}

.modal-extras {
  margin-bottom: 20px;
}

.modal-extras-info {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.extras-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.extra-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.extra-info {
  display: flex;
  flex-direction: column;
}

.extra-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.extra-price {
  font-size: 14px;
  color: #e74c3c;
}

.extra-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-quantity {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  background-color: #f0f0f0;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-quantity:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.btn-quantity:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.extra-quantity, .pizza-quantity {
  font-weight: 500;
  min-width: 20px;
  text-align: center;
}

.modal-quantity {
  margin-bottom: 20px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-top: 20px;
}

.total-label {
  font-size: 18px;
  font-weight: 600;
}

.total-price {
  font-size: 22px;
  font-weight: 700;
  color: #e74c3c;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel, .btn-add-to-cart {
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #333;
  border: none;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-add-to-cart {
  background-color: #e74c3c;
  color: white;
  border: none;
}

.btn-add-to-cart:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-add-to-cart:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

/* Toast notification */
.toast {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 4px;
  font-weight: 500;
  color: white;
  z-index: 9999;
  animation: slideIn 0.3s, fadeOut 0.3s 2.7s;
  max-width: 300px;
}

.toast.success {
  background-color: #2ecc71;
}

.toast.error {
  background-color: #e74c3c;
}

@keyframes slideIn {
  from { transform: translateX(100%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

@keyframes fadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}
</style>