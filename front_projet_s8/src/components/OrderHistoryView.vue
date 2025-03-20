<template>
  <div class="order-history-container">
    <h2 class="section-title">Mes Commandes</h2>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Chargement de vos commandes...</p>
    </div>

    <div v-else-if="orders.length === 0" class="empty-orders">
      <div class="empty-orders-icon">📦</div>
      <h3>Vous n'avez pas encore de commande</h3>
      <p>Vos commandes apparaîtront ici une fois que vous aurez passé une commande.</p>
      <router-link to="/pizzas" class="btn-primary">Commander des pizzas</router-link>
    </div>

    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <div class="order-number">Commande #{{ order.id }}</div>
            <div class="order-date">{{ formatDate(order.createdAt) }}</div>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusLabel(order.status) }}
          </div>
        </div>

        <div class="order-items">
          <div v-for="item in order.pizzaItems" :key="item.pizzaId" class="order-item">
            <div class="item-quantity">{{ item.quantity }}x</div>
            <div class="item-name">{{ item.name || `Pizza #${item.pizzaId}` }}</div>
            <div v-if="item.extraIngredients && item.extraIngredients.length > 0" class="item-extras">
              <div v-for="extra in item.extraIngredients" :key="extra.ingredientId" class="item-extra">
                + {{ extra.quantity }}x {{ extra.name || `Ingrédient #${extra.ingredientId}` }}
              </div>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">Total: {{ formatPrice(order.total) }} €</div>
          <button class="btn-details" @click="showOrderDetails(order)">Détails</button>
        </div>
      </div>
    </div>

    <!-- Modal des détails de commande -->
    <div v-if="showModal" class="order-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Détails de la commande #{{ selectedOrder.id }}</h3>
          <button class="close-modal" @click="closeModal">&times;</button>
        </div>

        <div class="modal-body">
          <div class="order-detail-section">
            <h4>Informations générales</h4>
            <div class="detail-row">
              <span class="detail-label">Commande passée le:</span>
              <span class="detail-value">{{ formatDate(selectedOrder.createdAt) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">Statut:</span>
              <span class="detail-value status" :class="getStatusClass(selectedOrder.status)">
                {{ getStatusLabel(selectedOrder.status) }}
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">Paiement:</span>
              <span class="detail-value">{{ selectedOrder.paid ? 'Payé' : 'En attente de paiement' }}</span>
            </div>
          </div>

          <div class="order-detail-section">
            <h4>Articles commandés</h4>
            <div class="detail-items">
              <div v-for="item in selectedOrder.pizzaItems" :key="item.pizzaId" class="detail-item">
                <div class="detail-item-header">
                  <span class="detail-item-name">{{ item.quantity }}x {{ item.name || `Pizza #${item.pizzaId}` }}</span>
                  <span class="detail-item-price">{{ formatPrice((item.unitPrice || 0) * item.quantity) }} €</span>
                </div>
                <div v-if="item.extraIngredients && item.extraIngredients.length > 0" class="detail-extras">
                  <div v-for="extra in item.extraIngredients" :key="extra.ingredientId" class="detail-extra">
                    <span class="detail-extra-name">+ {{ extra.quantity }}x {{ extra.name || `Ingrédient #${extra.ingredientId}` }}</span>
                    <span class="detail-extra-price">{{ formatPrice((extra.unitPrice || 0) * extra.quantity) }} €</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="order-summary">
              <div class="summary-row">
                <span>Sous-total:</span>
                <span>{{ formatPrice(calculateSubtotal(selectedOrder)) }} €</span>
              </div>
              <div class="summary-row">
                <span>Frais de livraison:</span>
                <span>{{ formatPrice(2.50) }} €</span>
              </div>
              <div class="summary-row total">
                <span>Total:</span>
                <span>{{ formatPrice(selectedOrder.total || calculateSubtotal(selectedOrder) + 2.50) }} €</span>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-close" @click="closeModal">Fermer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import OrderService from '@/services/OrderService';
import PizzaService from '@/services/PizzaService';
import IngredientService from '@/services/IngredientService';
import { ref, onMounted } from 'vue';

export default {
  name: 'OrderHistoryView',
  setup() {
    const loading = ref(true);
    const orders = ref([]);
    const showModal = ref(false);
    const selectedOrder = ref({});

    // Charger l'historique des commandes
    const loadOrders = async () => {
      try {
        loading.value = true;

        // Récupérer les commandes
        const userOrders = await OrderService.getUserOrders();

        // Récupérer les détails des pizzas et ingrédients
        const pizzas = await PizzaService.getAllPizzas();
        const ingredients = await IngredientService.getAllIngredients();

        // Enrichir les commandes avec les détails des produits
        orders.value = userOrders.map(order => {
          // Enrichir chaque pizza avec ses détails
          if (order.pizzaItems) {
            order.pizzaItems.forEach(item => {
              const pizzaDetails = pizzas.find(p => p.id === item.pizzaId);
              if (pizzaDetails) {
                item.name = pizzaDetails.name;
                item.unitPrice = pizzaDetails.prix;
              }

              // Enrichir chaque ingrédient supplémentaire avec ses détails
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
          }

          return order;
        });

        // Trier les commandes par date (la plus récente d'abord)
        orders.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      } catch (error) {
        console.error('Erreur lors du chargement des commandes:', error);
      } finally {
        loading.value = false;
      }
    };

    // Afficher les détails d'une commande
    const showOrderDetails = (order) => {
      selectedOrder.value = order;
      showModal.value = true;
    };

    // Fermer le modal
    const closeModal = () => {
      showModal.value = false;
    };

    // Formater la date
    const formatDate = (dateString) => {
      if (!dateString) return 'Date inconnue';

      const date = new Date(dateString);
      return new Intl.DateTimeFormat('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    };

    // Formater le prix
    const formatPrice = (price) => {
      return price ? price.toFixed(2) : '0.00';
    };

    // Obtenir la classe CSS en fonction du statut
    const getStatusClass = (status) => {
      if (!status) return '';

      switch (status.toLowerCase()) {
        case 'pending':
        case 'en attente':
          return 'status-pending';
        case 'preparing':
        case 'en préparation':
          return 'status-preparing';
        case 'shipping':
        case 'en livraison':
          return 'status-shipping';
        case 'delivered':
        case 'livrée':
          return 'status-delivered';
        case 'cancelled':
        case 'annulée':
          return 'status-cancelled';
        default:
          return '';
      }
    };

    // Obtenir le label en fonction du statut
    const getStatusLabel = (status) => {
      if (!status) return 'Statut inconnu';

      switch (status.toLowerCase()) {
        case 'pending':
          return 'En attente';
        case 'preparing':
          return 'En préparation';
        case 'shipping':
          return 'En livraison';
        case 'delivered':
          return 'Livrée';
        case 'cancelled':
          return 'Annulée';
        default:
          return status;
      }
    };

    // Calculer le sous-total d'une commande
    const calculateSubtotal = (order) => {
      if (!order || !order.pizzaItems) return 0;

      return order.pizzaItems.reduce((total, item) => {
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

    onMounted(() => {
      loadOrders();
    });

    return {
      loading,
      orders,
      showModal,
      selectedOrder,
      showOrderDetails,
      closeModal,
      formatDate,
      formatPrice,
      getStatusClass,
      getStatusLabel,
      calculateSubtotal
    };
  }
};
</script>

<style scoped>
.order-history-container {
  max-width: 900px;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px;
  text-align: center;
}

.empty-orders-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-orders h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.empty-orders p {
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

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-number {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.order-date {
  font-size: 14px;
  color: #666;
}

.order-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #f1c40f;
  color: #9a7d0a;
}

.status-preparing {
  background-color: #3498db;
  color: white;
}

.status-shipping {
  background-color: #9b59b6;
  color: white;
}

.status-delivered {
  background-color: #2ecc71;
  color: white;
}

.status-cancelled {
  background-color: #e74c3c;
  color: white;
}

.order-items {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.order-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-quantity {
  font-weight: 600;
  color: #666;
  margin-right: 10px;
}

.item-name {
  flex: 1;
}

.item-extras {
  margin-left: 25px;
  margin-top: 5px;
}

.item-extra {
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
}

.order-total {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.btn-details {
  background-color: #00c3ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-details:hover {
  background-color: #0099cc;
}

/* Modal */
.order-modal {
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
  width: 700px;
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
  font-size: 20px;
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
  padding: 20px;
}

.order-detail-section {
  margin-bottom: 25px;
}

.order-detail-section h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
}

.detail-label {
  width: 150px;
  font-weight: 500;
  color: #666;
}

.detail-value {
  flex: 1;
  color: #333;
}

.detail-value.status {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 14px;
}

.detail-items {
  margin-bottom: 20px;
}

.detail-item {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.detail-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.detail-item-name {
  font-weight: 500;
}

.detail-item-price {
  font-weight: 600;
}

.detail-extras {
  margin-left: 20px;
  margin-top: 5px;
}

.detail-extra {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.order-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.summary-row.total {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
  font-weight: 600;
  font-size: 18px;
}

.modal-footer {
  padding: 15px 20px;
  text-align: right;
  border-top: 1px solid #f0f0f0;
}

.btn-close {
  background-color: #f5f5f5;
  color: #333;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-close:hover {
  background-color: #e0e0e0;
}

@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-status {
    margin-top: 10px;
  }

  .order-footer {
    flex-direction: column;
    gap: 10px;
  }

  .detail-row {
    flex-direction: column;
  }

  .detail-label {
    width: 100%;
    margin-bottom: 5px;
  }
}
</style>