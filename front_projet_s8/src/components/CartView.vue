<template>
  <div class="cart-container">
    <h2 class="cart-title">Mon Panier</h2>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement du panier...</p>
    </div>

    <div v-else-if="!cartItems || cartItems.length === 0" class="empty-cart">
      <img src="https://cdn.pixabay.com/photo/2013/07/12/14/53/cart-148964_960_720.png" alt="Panier vide" class="empty-cart-icon" />
      <h3>Votre panier est vide</h3>
      <p>Ajoutez des pizzas à votre panier pour commencer votre commande.</p>
      <router-link to="/pizzas" class="btn-primary">Voir nos pizzas</router-link>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartItems" :key="item.pizzaId" class="cart-item">
          <div class="cart-item-details">
            <div class="item-image">
              <img v-if="pizzaImages[item.pizzaId]" :src="pizzaImages[item.pizzaId]" :alt="item.name" />
              <div v-else class="no-image">
                <span>Image non disponible</span>
              </div>
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ item.name || `Pizza #${item.pizzaId}` }}</h3>
              <p class="item-price">{{ formatPrice(item.unitPrice) }} €</p>
              <div class="item-quantity">
                <button class="quantity-btn" @click="decreaseQuantity(item)" :disabled="item.quantity <= 1">-</button>
                <span>{{ item.quantity }}</span>
                <button class="quantity-btn" @click="increaseQuantity(item)">+</button>
              </div>
            </div>
          </div>

          <div class="extra-ingredients" v-if="item.extraIngredients && item.extraIngredients.length > 0">
            <h4>Ingrédients supplémentaires:</h4>
            <ul>
              <li v-for="extra in item.extraIngredients" :key="extra.ingredientId">
                {{ extra.name || `Ingrédient #${extra.ingredientId}` }} ({{ extra.quantity }})
                <span class="extra-price">+{{ formatPrice(extra.unitPrice * extra.quantity) }} €</span>
                <button class="btn-remove-extra" @click="removeExtraIngredient(item.pizzaId, extra.ingredientId)">
                  <span class="icon-delete">×</span>
                </button>
              </li>
            </ul>
          </div>

          <div class="item-actions">
            <button class="btn-remove" @click="removePizza(item.pizzaId)">Supprimer</button>
          </div>
        </div>
      </div>

      <div class="cart-summary">
        <h3>Résumé de la commande</h3>
        <div class="summary-line">
          <span>Sous-total:</span>
          <span>{{ formatPrice(calculateSubtotal()) }} €</span>
        </div>
        <div class="summary-line total">
          <span>Total:</span>
          <span>{{ formatPrice(calculateSubtotal()) }} €</span>
        </div>
        <div class="cart-actions">
          <button class="btn-clear-cart" @click="confirmClearCart">Vider le panier</button>
          <button class="btn-checkout" @click="checkout">Passer la commande</button>
        </div>
      </div>
    </div>

    <!-- Modal de confirmation pour vider le panier -->
    <div class="modal" v-if="showClearCartModal">
      <div class="modal-content">
        <h3>Confirmation</h3>
        <p>Êtes-vous sûr de vouloir vider votre panier ?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showClearCartModal = false">Annuler</button>
          <button class="btn-confirm" @click="clearCart">Confirmer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CartService from '@/services/CartService';
import PizzaService from '@/services/PizzaService';
import IngredientService from '@/services/IngredientService';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'CartView',
  setup() {
    const router = useRouter();
    const loading = ref(true);
    const cart = ref(null);
    const cartItems = ref([]);
    const pizzas = ref([]);
    const ingredients = ref([]);
    const pizzaImages = ref({});
    const showClearCartModal = ref(false);
    const deliveryFee = 2.50; // Frais de livraison fixes

    // Charger le panier
    const loadCart = async () => {
      try {
        loading.value = true;
        cart.value = await CartService.getUserCart();
        cartItems.value = cart.value.pizzaItems || [];

        // Charger les détails des pizzas et les images
        await loadPizzaDetails();

        // Charger les détails des ingrédients supplémentaires
        await loadIngredientDetails();
      } catch (error) {
        console.error('Erreur lors du chargement du panier:', error);
        cartItems.value = [];
      } finally {
        loading.value = false;
      }
    };

    // Charger les détails des pizzas
    const loadPizzaDetails = async () => {
      try {
        pizzas.value = await PizzaService.getAllPizzas();

        // Associer les détails des pizzas aux items du panier
        cartItems.value.forEach(item => {
          const pizzaDetails = pizzas.value.find(p => p.id === item.pizzaId);
          if (pizzaDetails) {
            item.name = pizzaDetails.name;
            item.unitPrice = pizzaDetails.prix;
            pizzaImages.value[item.pizzaId] = pizzaDetails.photo;
          }
        });
      } catch (error) {
        console.error('Erreur lors du chargement des détails des pizzas:', error);
      }
    };

    // Charger les détails des ingrédients supplémentaires
    const loadIngredientDetails = async () => {
      try {
        ingredients.value = await IngredientService.getAllIngredients();

        // Associer les détails des ingrédients aux items du panier
        cartItems.value.forEach(item => {
          if (item.extraIngredients) {
            item.extraIngredients.forEach(extra => {
              const ingredientDetails = ingredients.value.find(i => i.id === extra.ingredientId);
              if (ingredientDetails) {
                extra.name = ingredientDetails.name;
                extra.unitPrice = ingredientDetails.prix;
              }
            });
          }
        });
      } catch (error) {
        console.error('Erreur lors du chargement des détails des ingrédients:', error);
      }
    };

    // Augmenter la quantité d'une pizza
    const increaseQuantity = async (item) => {
      try {
        await CartService.addPizzaToCart(item.pizzaId, 1);
        item.quantity += 1;
      } catch (error) {
        console.error('Erreur lors de l\'augmentation de la quantité:', error);
      }
    };

    // Diminuer la quantité d'une pizza
    const decreaseQuantity = async (item) => {
      if (item.quantity <= 1) return;

      try {
        // On simule une diminution avec l'API de suppression et l'ajout
        await CartService.removePizzaFromCart(item.pizzaId);
        await CartService.addPizzaToCart(item.pizzaId, item.quantity - 1);
        item.quantity -= 1;
      } catch (error) {
        console.error('Erreur lors de la diminution de la quantité:', error);
      }
    };

    // Supprimer une pizza du panier
    const removePizza = async (pizzaId) => {
      try {
        await CartService.removePizzaFromCart(pizzaId);
        cartItems.value = cartItems.value.filter(item => item.pizzaId !== pizzaId);
      } catch (error) {
        console.error('Erreur lors de la suppression de la pizza:', error);
      }
    };

    // Supprimer un ingrédient supplémentaire
    const removeExtraIngredient = async (pizzaId, ingredientId) => {
      try {
        await CartService.removeExtraIngredientFromCart(pizzaId, ingredientId);

        // Mettre à jour l'UI en supprimant l'ingrédient
        const pizzaItem = cartItems.value.find(item => item.pizzaId === pizzaId);
        if (pizzaItem && pizzaItem.extraIngredients) {
          pizzaItem.extraIngredients = pizzaItem.extraIngredients.filter(
              extra => extra.ingredientId !== ingredientId
          );
        }
      } catch (error) {
        console.error('Erreur lors de la suppression de l\'ingrédient supplémentaire:', error);
      }
    };

    // Confirmer la suppression du panier
    const confirmClearCart = () => {
      showClearCartModal.value = true;
    };

    // Vider le panier
    const clearCart = async () => {
      try {
        await CartService.clearCart();
        cartItems.value = [];
        showClearCartModal.value = false;
      } catch (error) {
        console.error('Erreur lors du vidage du panier:', error);
      }
    };

    // Calculer le sous-total du panier
    const calculateSubtotal = () => {
      return cartItems.value.reduce((total, item) => {
        // Prix de base de la pizza
        let itemTotal = (item.unitPrice || 0) * item.quantity;

        // Ajouter le prix des ingrédients supplémentaires
        if (item.extraIngredients) {
          itemTotal += item.extraIngredients.reduce((extraTotal, extra) => {
            return extraTotal + (extra.unitPrice || 0) * extra.quantity;
          }, 0);
        }

        return total + itemTotal;
      }, 0);
    };

    // Formater le prix
    const formatPrice = (price) => {
      return price ? price.toFixed(2) : '0.00';
    };

    // Passer à la commande
    const checkout = () => {
      router.push('/checkout');
    };

    onMounted(() => {
      loadCart();
    });

    return {
      loading,
      cart,
      cartItems,
      pizzaImages,
      deliveryFee,
      showClearCartModal,
      increaseQuantity,
      decreaseQuantity,
      removePizza,
      removeExtraIngredient,
      confirmClearCart,
      clearCart,
      calculateSubtotal,
      formatPrice,
      checkout
    };
  }
};
</script>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cart-title {
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px;
  text-align: center;
}

.empty-cart-icon {
  width: 150px;
  height: auto;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-cart h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.empty-cart p {
  color: #666;
  margin-bottom: 20px;
}

.btn-primary {
  display: inline-block;
  background-color: #00c3ff;
  color: white;
  padding: 12px 24px;
  border-radius: 4px;
  font-weight: 500;
  text-decoration: none;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #0099cc;
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (min-width: 768px) {
  .cart-content {
    flex-direction: row;
  }

  .cart-items {
    flex: 2;
  }

  .cart-summary {
    flex: 1;
  }
}

.cart-items {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.cart-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-details {
  display: flex;
  gap: 20px;
}

.item-image {
  width: 100px;
  height: 100px;
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-style: italic;
  font-size: 12px;
  text-align: center;
  padding: 10px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 18px;
  color: #333;
  margin-bottom: 5px;
}

.item-price {
  font-size: 18px;
  font-weight: 600;
  color: #e74c3c;
  margin-bottom: 10px;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.quantity-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.extra-ingredients {
  margin-top: 15px;
  background-color: #f9f9f9;
  padding: 10px 15px;
  border-radius: 4px;
}

.extra-ingredients h4 {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.extra-ingredients ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.extra-ingredients li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 0;
  font-size: 14px;
  color: #333;
}

.extra-price {
  color: #e74c3c;
  font-weight: 500;
  margin-left: auto;
  margin-right: 10px;
}

.btn-remove-extra {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.btn-remove-extra:hover {
  color: #e74c3c;
}

.icon-delete {
  font-weight: bold;
}

.item-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.btn-remove {
  background-color: #f5f5f5;
  color: #666;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-remove:hover {
  background-color: #e74c3c;
  color: white;
}

.cart-summary {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  position: sticky;
  top: 20px;
}

.cart-summary h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
  color: #666;
}

.delivery-fee {
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.total {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-top: 10px;
  margin-bottom: 20px;
}

.cart-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn-checkout {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-checkout:hover {
  background-color: #c0392b;
}

.btn-clear-cart {
  background-color: transparent;
  color: #666;
  border: 1px solid #ddd;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-clear-cart:hover {
  background-color: #f5f5f5;
  border-color: #ccc;
}

/* Modal */
.modal {
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
  padding: 20px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
}

.modal-content p {
  margin-bottom: 20px;
  color: #666;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel, .btn-confirm {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #333;
  border: none;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-confirm {
  background-color: #e74c3c;
  color: white;
  border: none;
}

.btn-confirm:hover {
  background-color: #c0392b;
}
</style>