<template>
  <div class="account-container">
    <div class="account-header">
      <h1>Mon Compte</h1>
      <p>Gérez vos informations personnelles et vos commandes</p>
    </div>

    <div class="account-card">
      <h2>Informations personnelles</h2>
      <div class="form-group">
        <label for="firstName">Prénom</label>
        <input type="text" id="firstName" v-model="userData.firstName" :disabled="!editMode">
      </div>
      <div class="form-group">
        <label for="lastName">Nom</label>
        <input type="text" id="lastName" v-model="userData.lastName" :disabled="!editMode">
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" v-model="userData.email" :disabled="!editMode">
      </div>
      <div class="form-group" v-if="editMode">
        <label for="password">Nouveau mot de passe</label>
        <input type="password" id="password" v-model="userData.password" placeholder="Laisser vide pour ne pas changer">
      </div>
      <div class="action-buttons">
        <button v-if="!editMode" @click="toggleEditMode" class="btn btn-primary">Modifier</button>
        <template v-else>
          <button @click="saveUserData" class="btn btn-success">Enregistrer</button>
          <button @click="cancelEdit" class="btn btn-secondary">Annuler</button>
        </template>
      </div>
    </div>

    <div class="account-card">
      <h2>Mes commandes récentes</h2>
      <div v-if="orders.length === 0" class="no-orders">
        <p>Vous n'avez pas encore passé de commande.</p>
        <router-link to="/pizzas" class="btn btn-primary">Commander maintenant</router-link>
      </div>
      <div v-else class="orders-list">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-number">Commande #{{ order.id }}</span>
            <span class="order-date">{{ formatDate(order.date) }}</span>
            <span :class="['order-status', getStatusClass(order.status)]">{{ order.status }}</span>
          </div>
          <div class="order-content">
            <div class="order-products">
              <span v-for="(item, index) in order.items" :key="index">
                {{ item.quantity }}x {{ item.name }}{{ index < order.items.length - 1 ? ', ' : '' }}
              </span>
            </div>
            <div class="order-total">
              <span>Total: {{ order.total.toFixed(2) }} €</span>
            </div>
          </div>
          <div class="order-actions">
            <router-link :to="`/compte/commandes/${order.id}`" class="btn btn-outline">Détails</router-link>
          </div>
        </div>
      </div>
      <div class="view-all-orders">
        <router-link to="/compte/commandes" class="btn btn-text">Voir toutes mes commandes</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService'
import OrderService from '@/services/OrderService'
import { ref, reactive, onMounted } from 'vue'
//import { useRouter } from 'vue-router'

export default {
  name: 'UserAccountView',
  setup() {
    //const router = useRouter()
    const userData = reactive({
      firstName: '',
      lastName: '',
      email: '',
      password: ''
    })

    const orders = ref([])
    const editMode = ref(false)

    onMounted(async () => {
      try {
        // Récupérer les données de l'utilisateur
        const userDetails = await AuthService.getUserDetails()
        userData.firstName = userDetails.firstName || ''
        userData.lastName = userDetails.lastName || ''
        userData.email = userDetails.email || ''

        // Récupérer les commandes récentes (5 dernières)
        const userOrders = await OrderService.getUserOrders(5)
        orders.value = userOrders
      } catch (error) {
        console.error('Erreur lors du chargement des données:', error)
      }
    })

    const toggleEditMode = () => {
      editMode.value = !editMode.value
    }

    const saveUserData = async () => {
      try {
        await AuthService.updateUserDetails(userData)
        editMode.value = false
        // Réinitialiser le mot de passe après la sauvegarde
        userData.password = ''
        alert('Vos informations ont été mises à jour avec succès')
      } catch (error) {
        console.error('Erreur lors de la mise à jour:', error)
        alert('Erreur lors de la mise à jour de vos informations')
      }
    }

    const cancelEdit = () => {
      // Recharger les données originales
      AuthService.getUserDetails().then(userDetails => {
        userData.firstName = userDetails.firstName || ''
        userData.lastName = userDetails.lastName || ''
        userData.email = userDetails.email || ''
        userData.password = ''
      })
      editMode.value = false
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    }

    const getStatusClass = (status) => {
      const statusMap = {
        'en attente': 'pending',
        'en préparation': 'processing',
        'en livraison': 'shipping',
        'livrée': 'delivered',
        'annulée': 'cancelled'
      }
      return statusMap[status.toLowerCase()] || 'pending'
    }

    return {
      userData,
      orders,
      editMode,
      toggleEditMode,
      saveUserData,
      cancelEdit,
      formatDate,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.account-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.account-header {
  text-align: center;
  margin-bottom: 30px;
}

.account-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.account-header p {
  color: #666;
  font-size: 16px;
}

.account-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 24px;
}

.account-card h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  color: #666;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 18px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s, transform 0.1s;
}

.btn:hover {
  transform: translateY(-1px);
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
}

.btn-success {
  background-color: #2ecc71;
  color: white;
}

.btn-success:hover {
  background-color: #27ae60;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #3498db;
  color: #3498db;
}

.btn-outline:hover {
  background-color: #f5f9fc;
}

.btn-text {
  background-color: transparent;
  color: #3498db;
  padding: 0;
  text-decoration: underline;
}

.btn-text:hover {
  color: #2980b9;
  transform: none;
}

.no-orders {
  text-align: center;
  padding: 30px 0;
}

.no-orders p {
  margin-bottom: 16px;
  color: #666;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 6px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f9f9f9;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.order-number {
  font-weight: bold;
  color: #333;
}

.order-date {
  color: #666;
}

.order-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.order-status.pending {
  background-color: #ffeaa7;
  color: #d35400;
}

.order-status.processing {
  background-color: #81ecec;
  color: #0984e3;
}

.order-status.shipping {
  background-color: #a29bfe;
  color: #6c5ce7;
}

.order-status.delivered {
  background-color: #55efc4;
  color: #00b894;
}

.order-status.cancelled {
  background-color: #fab1a0;
  color: #d63031;
}

.order-content {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-products {
  color: #666;
  flex: 1;
}

.order-total {
  font-weight: bold;
  color: #333;
}

.order-actions {
  padding: 0 16px 16px;
  text-align: right;
}

.view-all-orders {
  margin-top: 16px;
  text-align: center;
}
</style>