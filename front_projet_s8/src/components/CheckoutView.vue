<template>
  <div class="checkout-container">
    <h2 class="checkout-title">Finaliser la commande</h2>

    <div class="checkout-steps">
      <div class="step" :class="{ 'active': currentStep === 1, 'completed': currentStep > 1 }">
        <div class="step-number">1</div>
        <div class="step-label">Récapitulatif</div>
      </div>
      <div class="step-connector"></div>
      <div class="step" :class="{ 'active': currentStep === 2, 'completed': currentStep > 2 }">
        <div class="step-number">2</div>
        <div class="step-label">Paiement</div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement des informations...</p>
    </div>

    <div v-else class="checkout-content">
      <!-- Étape 1: Récapitulatif de la commande -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="order-summary">
          <h3>Récapitulatif de votre commande</h3>

          <div v-if="!cartItems || cartItems.length === 0" class="empty-cart-message">
            <p>Votre panier est vide. Veuillez ajouter des produits avant de passer commande.</p>
            <router-link to="/pizzas" class="btn-primary">Voir nos pizzas</router-link>
          </div>

          <div v-else>
            <div class="cart-items-summary">
              <div v-for="item in cartItems" :key="item.pizzaId" class="cart-item-summary">
                <div class="item-summary-details">
                  <span class="item-quantity">{{ item.quantity }}x</span>
                  <span class="item-name">{{ item.name || `Pizza #${item.pizzaId}` }}</span>
                  <span class="item-price">{{ formatPrice((item.unitPrice || 0) * item.quantity) }} €</span>
                </div>

                <div v-if="item.extraIngredients && item.extraIngredients.length > 0" class="item-extras">
                  <div v-for="extra in item.extraIngredients" :key="extra.ingredientId" class="item-extra">
                    <span class="extra-detail">
                      + {{ extra.quantity }}x {{ extra.name || `Ingrédient #${extra.ingredientId}` }}
                    </span>
                    <span class="extra-price">{{ formatPrice((extra.unitPrice || 0) * extra.quantity) }} €</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="price-summary">
              <div class="summary-line">
                <span>Total:</span>
                <span>{{ formatPrice(calculateSubtotal()) }} €</span>
              </div>
            </div>

            <div class="pickup-info">
              <h4>Information Click & Collect</h4>
              <p>Votre commande sera disponible pour retrait en magasin dans les 30 minutes après confirmation.</p>
              <p>Adresse: 12 rue de BoneMeal, 29200 Brest</p>
            </div>

            <div class="form-group">
              <label for="pickupTime">Heure de retrait souhaitée</label>
              <select id="pickupTime" v-model="pickupInfo.time" required>
                <option v-for="time in availablePickupTimes" :key="time" :value="time">
                  {{ time }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="comments">Instructions spéciales (optionnel)</label>
              <textarea
                  id="comments"
                  v-model="pickupInfo.comments"
                  placeholder="Instructions particulières..."
                  rows="3"
              ></textarea>
            </div>

            <div class="step-actions">
              <router-link to="/panier" class="btn-back">Retour au panier</router-link>
              <button class="btn-next" @click="goToNextStep">Continuer</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Étape 2: Paiement -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="payment-info">
          <h3>Méthode de paiement</h3>

          <form @submit.prevent="placeOrder" class="payment-form">
            <div class="payment-methods">
              <div class="payment-method" :class="{ 'selected': paymentMethod === 'card' }" @click="paymentMethod = 'card'">
                <input type="radio" id="card" name="paymentMethod" value="card" v-model="paymentMethod" />
                <label for="card">
                  <span class="payment-icon">💳</span>
                  <span>Carte bancaire</span>
                </label>
              </div>

              <div class="payment-method" :class="{ 'selected': paymentMethod === 'paypal' }" @click="paymentMethod = 'paypal'">
                <input type="radio" id="paypal" name="paymentMethod" value="paypal" v-model="paymentMethod" />
                <label for="paypal">
                  <span class="payment-icon">🅿️</span>
                  <span>PayPal</span>
                </label>
              </div>

              <div class="payment-method" :class="{ 'selected': paymentMethod === 'cash' }" @click="paymentMethod = 'cash'">
                <input type="radio" id="cash" name="paymentMethod" value="cash" v-model="paymentMethod" />
                <label for="cash">
                  <span class="payment-icon">💶</span>
                  <span>Espèces au retrait</span>
                </label>
              </div>
            </div>

            <div v-if="paymentMethod === 'card'" class="card-details">
              <div class="form-group">
                <label for="cardNumber">Numéro de carte</label>
                <input
                    type="text"
                    id="cardNumber"
                    v-model="paymentInfo.cardNumber"
                    placeholder="1234 5678 9012 3456"
                    required
                />
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label for="expiryDate">Date d'expiration</label>
                  <input
                      type="text"
                      id="expiryDate"
                      v-model="paymentInfo.expiryDate"
                      placeholder="MM/AA"
                      required
                  />
                </div>

                <div class="form-group">
                  <label for="cvv">CVV</label>
                  <input
                      type="text"
                      id="cvv"
                      v-model="paymentInfo.cvv"
                      placeholder="123"
                      required
                  />
                </div>
              </div>

              <div class="form-group">
                <label for="cardName">Nom sur la carte</label>
                <input
                    type="text"
                    id="cardName"
                    v-model="paymentInfo.cardName"
                    placeholder="Nom du titulaire"
                    required
                />
              </div>
            </div>

            <div class="order-summary-review">
              <h4>Récapitulatif</h4>
              <div class="summary-details">
                <p><strong>Heure de retrait:</strong> {{ pickupInfo.time }}</p>
                <p v-if="pickupInfo.comments"><strong>Instructions:</strong> {{ pickupInfo.comments }}</p>
              </div>
            </div>

            <div class="order-total">
              <div class="total-line">
                <span>Total à payer:</span>
                <span class="total-amount">{{ formatPrice(calculateSubtotal()) }} €</span>
              </div>
            </div>

            <div class="step-actions">
              <button type="button" class="btn-back" @click="goToPreviousStep">Retour</button>
              <button type="submit" class="btn-order" :disabled="isProcessing">
                {{ isProcessing ? 'Traitement en cours...' : 'Confirmer la commande' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Confirmation de commande -->
      <div v-if="orderPlaced" class="order-confirmation">
        <div class="confirmation-icon">✅</div>
        <h3>Commande confirmée !</h3>
        <p>Votre commande a été passée avec succès.</p>
        <p class="order-number">Numéro de commande: <strong>{{ orderNumber }}</strong></p>
        <div class="pickup-confirmation">
          <p><strong>À récupérer à:</strong> {{ pickupInfo.time }}</p>
          <p>Adresse: 12 rue de BoneMeal</p>
          <p>Un récapitulatif a été envoyé à votre adresse e-mail.</p>
        </div>
        <div class="confirmation-actions">
          <router-link to="/" class="btn-primary">Retour à l'accueil</router-link>
          <router-link to="/compte/commandes" class="btn-secondary">Voir mes commandes</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CartService from '@/services/CartService';
import OrderService from '@/services/OrderService';
import PizzaService from '@/services/PizzaService';
import IngredientService from '@/services/IngredientService';
import AuthService from '@/services/AuthService';
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
const currentUser = ref(null);

// 1. Dans la méthode loadUserInfo, ajoutez des logs détaillés:
const loadUserInfo = async () => {
  try {
    console.log('Tentative de récupération des informations utilisateur...');
    // Récupérer l'utilisateur connecté
    const userData = await AuthService.getCurrentUser();
    console.log('Réponse brute de getCurrentUser:', userData);

    if (userData) {
      currentUser.value = userData;
      console.log('Informations utilisateur chargées avec succès:', {
        id: userData._id || userData.userId,
        firstName: userData.firstName,
        lastName: userData.lastName,
        email: userData.email
      });
    } else {
      console.warn('Aucune donnée utilisateur reçue');
    }
  } catch (error) {
    console.error('Erreur lors du chargement des informations utilisateur:', error);
  }
};

export default {
  name: 'CheckoutView',
  setup() {
    const router = useRouter();
    const loading = ref(true);
    const cart = ref(null);
    const cartItems = ref([]);
    const currentStep = ref(1);
    const orderPlaced = ref(false);
    const orderNumber = ref('');
    const isProcessing = ref(false);

    // Informations de retrait
    const pickupInfo = ref({
      time: '',
      comments: ''
    });

    // Génération des horaires de retrait disponibles
    const availablePickupTimes = computed(() => {
      const times = [];
      const now = new Date();
      const currentHour = now.getHours();
      const currentMinute = now.getMinutes();

      // Supposons que le magasin soit ouvert de 11h à 22h
      const storeOpenHour = 11;
      const storeCloseHour = 22;

      // Commençons à la prochaine demi-heure disponible
      let startHour = currentHour;
      let startMinute = currentMinute < 30 ? 30 : 0;

      if (currentMinute >= 30) {
        startHour += 1;
      }

      // Ajoutons 30 minutes pour la préparation
      if (startMinute === 30) {
        startHour += 1;
        startMinute = 0;
      } else {
        startMinute = 30;
      }

      // Si nous sommes en dehors des heures d'ouverture, commençons demain
      const tomorrow = new Date(now);
      tomorrow.setDate(tomorrow.getDate() + 1);
      const isNextDay = startHour >= storeCloseHour || startHour < storeOpenHour;

      // Générer les horaires disponibles
      const day = isNextDay ? tomorrow : now;
      const dayStr = day.toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' });

      const startTimeHour = isNextDay ? storeOpenHour : startHour;
      const endTimeHour = storeCloseHour;

      for (let h = startTimeHour; h < endTimeHour; h++) {
        for (let m of [0, 30]) {
          // Ignorer la première plage si c'est aujourd'hui et nous commençons à la demi-heure
          if (h === startTimeHour && m < startMinute && !isNextDay) continue;

          const hour = h.toString().padStart(2, '0');
          const minute = m.toString().padStart(2, '0');

          times.push(`${dayStr} à ${hour}:${minute}`);
        }
      }

      return times;
    });

    // Définir l'heure de retrait par défaut
    onMounted(() => {
      if (availablePickupTimes.value.length > 0) {
        pickupInfo.value.time = availablePickupTimes.value[0];
      }
    });

    // Méthode de paiement
    const paymentMethod = ref('card');

    // Informations de paiement
    const paymentInfo = ref({
      cardNumber: '',
      expiryDate: '',
      cvv: '',
      cardName: ''
    });

    // Charger le panier
    const loadCart = async () => {
      try {
        loading.value = true;
        cart.value = await CartService.getUserCart();
        cartItems.value = cart.value.pizzaItems || [];
        // Charger les informations de l'utilisateur
        await loadUserInfo();
        // Charger les détails des pizzas
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
        const pizzas = await PizzaService.getAllPizzas();

        // Associer les détails des pizzas aux items du panier
        cartItems.value.forEach(item => {
          const pizzaDetails = pizzas.find(p => p.id === item.pizzaId);
          if (pizzaDetails) {
            item.name = pizzaDetails.name;
            item.unitPrice = pizzaDetails.prix;
          }
        });
      } catch (error) {
        console.error('Erreur lors du chargement des détails des pizzas:', error);
      }
    };

    // Charger les détails des ingrédients supplémentaires
    const loadIngredientDetails = async () => {
      try {
        const ingredients = await IngredientService.getAllIngredients();

        // Associer les détails des ingrédients aux items du panier
        cartItems.value.forEach(item => {
          if (item.extraIngredients) {
            item.extraIngredients.forEach(extra => {
              const ingredientDetails = ingredients.find(i => i.id === extra.ingredientId);
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

    // Passer à l'étape suivante
    const goToNextStep = () => {
      if (currentStep.value < 2) {
        currentStep.value += 1;
        window.scrollTo(0, 0);
      }
    };

    // Revenir à l'étape précédente
    const goToPreviousStep = () => {
      if (currentStep.value > 1) {
        currentStep.value -= 1;
        window.scrollTo(0, 0);
      }
    };

    // Passer la commande
    const placeOrder = async () => {
      // Vérifier si le panier est vide
      if (!cartItems.value || cartItems.value.length === 0) {
        alert('Votre panier est vide. Veuillez ajouter des produits avant de passer commande.');
        return;
      }

      // Validation des informations de paiement pour la carte
      if (paymentMethod.value === 'card') {
        if (!paymentInfo.value.cardNumber || !paymentInfo.value.expiryDate || !paymentInfo.value.cvv || !paymentInfo.value.cardName) {
          alert('Veuillez remplir tous les champs de paiement.');
          return;
        }
      }

      try {
        isProcessing.value = true;

        // Créer l'objet de commande
        const orderData = {
          userId: null, // Sera rempli par le backend
          pizzaItems: cartItems.value.map(item => ({  // Utilisez "pizzaItems" au lieu de "orderItems"
            pizzaId: item.pizzaId,
            quantity: item.quantity,
            extraIngredients: item.extraIngredients || []
          })),
          subtotal: calculateSubtotal(),
          total: calculateSubtotal(),
          name: currentUser.value ?
              `${currentUser.value.firstName || ''} ${currentUser.value.lastName || ''}`.trim() :
              null,
          // Les autres champs peuvent rester, mais le backend ne les utilisera pas s'ils ne sont pas dans le DTO
          pickupInfo: pickupInfo.value,
          paymentMethod: paymentMethod.value,
          paymentInfo: paymentMethod.value === 'card' ? paymentInfo.value : null,
          orderType: 'clickAndCollect',
          status: 'pending'
        };

        // Envoyer la commande au serveur
        const response = await OrderService.createOrder(orderData);

        // Vider le panier après commande réussie
        await CartService.clearCart();

        // Mettre à jour le compteur du panier dans la navbar si la fonction existe
        if (window.updateNavbarCart) {
          window.updateNavbarCart();
        }

        // Afficher la confirmation de commande
        orderNumber.value = response.id || 'ORDER-' + Math.floor(Math.random() * 10000);
        orderPlaced.value = true;

        // Rediriger après quelques secondes
        setTimeout(() => {
          router.push('/compte/commandes');
        }, 8000);

      } catch (error) {
        console.error('Erreur lors de la création de la commande:', error);
        alert('Une erreur est survenue lors du traitement de votre commande. Veuillez réessayer plus tard.');
      } finally {
        isProcessing.value = false;
      }
    };

    onMounted(() => {
      loadCart();
    });

    return {
      loading,
      cartItems,
      currentStep,
      pickupInfo,
      availablePickupTimes,
      paymentMethod,
      paymentInfo,
      orderPlaced,
      orderNumber,
      isProcessing,
      currentUser,
      calculateSubtotal,
      formatPrice,
      goToNextStep,
      goToPreviousStep,
      placeOrder
    };
  }
};
</script>

<style scoped>
.checkout-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.checkout-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.checkout-steps {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 40px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-bottom: 10px;
  transition: all 0.3s;
}

.step.active .step-number {
  background-color: #00c3ff;
  color: white;
}

.step.completed .step-number {
  background-color: #27ae60;
  color: white;
}

.step-label {
  font-size: 14px;
  color: #666;
  transition: color 0.3s;
}

.step.active .step-label,
.step.completed .step-label {
  color: #333;
  font-weight: 500;
}

.step-connector {
  width: 80px;
  height: 2px;
  background-color: #f0f0f0;
  margin: 0 10px;
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

.checkout-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.step-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Récapitulatif de la commande */
.order-summary h3,
.payment-info h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.empty-cart-message {
  text-align: center;
  padding: 30px 0;
}

.empty-cart-message p {
  margin-bottom: 20px;
  color: #666;
}

.cart-items-summary {
  margin-bottom: 20px;
}

.cart-item-summary {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item-summary:last-child {
  border-bottom: none;
}

.item-summary-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.item-quantity {
  font-weight: 600;
  color: #666;
  margin-right: 10px;
}

.item-name {
  flex: 1;
  text-align: left;
}

.item-price {
  font-weight: 600;
  color: #333;
}

.item-extras {
  padding-left: 30px;
  margin-top: 5px;
}

.item-extra {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.extra-detail {
  flex: 1;
}

.extra-price {
  color: #e74c3c;
}

.price-summary {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
  color: #666;
}

.total {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-top: 10px;
}

.pickup-info {
  margin-top: 25px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
  border-left: 4px solid #00c3ff;
}

.pickup-info h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
}

.pickup-info p {
  margin-bottom: 5px;
  color: #666;
}

.step-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.btn-back,
.btn-next,
.btn-order,
.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  display: inline-block;
}

.btn-back {
  background-color: #f5f5f5;
  color: #333;
  border: none;
}

.btn-back:hover {
  background-color: #e0e0e0;
}

.btn-next {
  background-color: #00c3ff;
  color: white;
  border: none;
}

.btn-next:hover {
  background-color: #0099cc;
}

.btn-order {
  background-color: #e74c3c;
  color: white;
  border: none;
}

.btn-order:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-order:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #00c3ff;
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: #0099cc;
}

.btn-secondary {
  background-color: white;
  color: #00c3ff;
  border: 1px solid #00c3ff;
}

.btn-secondary:hover {
  background-color: #f0f9ff;
}

/* Formulaire */
.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

input,
textarea,
select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

input:focus,
textarea:focus,
select:focus {
  border-color: #00c3ff;
  outline: none;
}

/* Méthodes de paiement */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 25px;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method.selected {
  border-color: #00c3ff;
  background-color: #f0f9ff;
}

.payment-method input {
  margin-right: 15px;
  width: auto;
}

.payment-method label {
  cursor: pointer;
  display: flex;
  align-items: center;
  margin: 0;
  width: 100%;
}

.payment-icon {
  margin-right: 10px;
  font-size: 20px;
}

.card-details {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.order-summary-review {
  margin-top: 25px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
  border-left: 4px solid #27ae60;
}

.order-summary-review h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
}

.summary-details p {
  margin-bottom: 5px;
  color: #666;
}

.order-total {
  margin-top: 30px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.total-amount {
  color: #e74c3c;
  font-size: 22px;
}

/* Confirmation de commande */
.order-confirmation {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  text-align: center;
}

.confirmation-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.order-confirmation h3 {
  font-size: 24px;
  color: #27ae60;
  margin-bottom: 15px;
}

.order-confirmation p {
  margin-bottom: 10px;
  color: #666;
}

.order-number {
  margin: 20px 0;
  padding: 10px 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
  font-size: 18px;
}

.pickup-confirmation {
  margin: 20px 0;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 6px;
  border-left: 4px solid #27ae60;
  text-align: left;
  width: 100%;
  max-width: 400px;
}

.pickup-confirmation p {
  margin-bottom: 8px;
}

.confirmation-actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .confirmation-actions {
    flex-direction: column;
  }

  .checkout-steps {
    margin-bottom: 20px;
  }

  .step-connector {
    width: 40px;
  }

  .checkout-content {
    padding: 20px;
  }
}
</style>