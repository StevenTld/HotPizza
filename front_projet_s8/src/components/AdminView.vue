<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1 class="admin-title">Bienvenue dans l'espace d'administration</h1>
      <p class="admin-subtitle">Gérez votre catalogue et suivez vos commandes</p>
    </div>

    <div class="admin-dashboard">
      <div class="admin-card" @click="activeSection = 'ingredients'" :class="{ 'active': activeSection === 'ingredients' }">
        <div class="card-icon">
          <i class="fas fa-pepper-hot"></i>
        </div>
        <div class="card-content">
          <h3>Gestion des Ingrédients</h3>
          <p>Ajouter, modifier ou supprimer des ingrédients</p>
        </div>
      </div>

      <div class="admin-card" @click="activeSection = 'pizzas'" :class="{ 'active': activeSection === 'pizzas' }">
        <div class="card-icon">
          <i class="fas fa-pizza-slice"></i>
        </div>
        <div class="card-content">
          <h3>Gestion des Pizzas</h3>
          <p>Ajouter, modifier ou supprimer des pizzas</p>
        </div>
      </div>

      <div class="admin-card" @click="activeSection = 'orders'" :class="{ 'active': activeSection === 'orders' }">
        <div class="card-icon">
          <i class="fas fa-receipt"></i>
        </div>
        <div class="card-content">
          <h3>Commandes du jour</h3>
          <p>Consulter et gérer les commandes en cours</p>
        </div>
      </div>
    </div>

    <div class="admin-content">
      <div v-if="activeSection === 'ingredients'">
        <Gestion_Ingredient />
      </div>

      <div v-if="activeSection === 'pizzas'">
        <Gestion_Pizza />
      </div>

      <div v-if="activeSection === 'orders'">
        <div class="orders-section">
          <h2 class="section-title">Commandes du jour</h2>

          <div class="orders-stats">
            <div class="stat-card">
              <div class="stat-value">{{ ordersCount }}</div>
              <div class="stat-label">Commandes</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ pendingOrdersCount }}</div>
              <div class="stat-label">En cours</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ deliveredOrdersCount }}</div>
              <div class="stat-label">Terminées</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ totalRevenue }}€</div>
              <div class="stat-label">Chiffre d'affaires</div>
            </div>
          </div>

          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>Chargement des commandes...</p>
          </div>

          <div v-else-if="error" class="error-message">
            {{ error }}
          </div>

          <!-- Remplacez cette partie dans votre template -->
          <div v-else class="orders-table-container">
            <table class="orders-table">
              <thead>
              <tr>
                <th>N° Commande</th>
                <th>Client</th>
                <th>Heure</th>
                <th>Total</th>
                <th>Statut</th>
                <th>Actions</th>
              </tr>
              </thead>
              <tbody>
              <!-- Remplacez todayOrders par sortedOrders -->
              <tr v-for="order in sortedOrders" :key="order.id" :class="{ 'completed': order.status === 'Terminée' }">
                <td>#{{ order.id }}</td>
                <td>{{ order.name || order.userId || 'Client' }}</td>
                <td>{{ formatTime(order.createdAt || order.orderTime) }}</td>
                <td>{{ typeof order.total === 'number' ? order.total.toFixed(2) : parseFloat(order.total).toFixed(2) }}€</td>
                <td>
          <span class="status-badge" :class="getStatusClass(order.status)">
            {{ order.status }}
          </span>
                </td>
                <td class="actions-cell">
                  <button class="btn-view" @click="viewOrderDetails(order)">Détails</button>
                  <button
                      class="btn-status"
                      :class="{ 'btn-status-completed': order.status === 'Terminée' }"
                      @click="updateOrderStatus(order)"
                      :disabled="order.status === 'Terminée'"
                  >
                    {{ order.status === 'Terminée' ? 'Terminée' : 'Marquer terminée' }}
                  </button>
                </td>
              </tr>
              <tr v-if="sortedOrders.length === 0">
                <td colspan="6" class="no-orders">Aucune commande pour aujourd'hui</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- Modal des détails de commande -->
  <div v-if="showOrderDetailsModal && selectedOrderForDetails" class="order-details-modal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Commande #{{ selectedOrderForDetails.id }}</h2>
        <div class="order-status" :class="getStatusClass(selectedOrderForDetails.status)">
          {{ selectedOrderForDetails.status }}
        </div>
        <button @click="closeOrderDetailsModal" class="close-button">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <div class="order-info-grid">
          <div class="order-info-card">
            <div class="info-icon"><i class="fas fa-user"></i></div>
            <div class="info-content">
              <div class="info-label">Client</div>
              <div class="info-value">{{ selectedOrderForDetails.lastName || selectedOrderForDetails.userId || 'Client anonyme' }}</div>
            </div>
          </div>

          <div class="order-info-card">
            <div class="info-icon"><i class="fas fa-calendar-alt"></i></div>
            <div class="info-content">
              <div class="info-label">Date</div>
              <div class="info-value">{{ formatDate(selectedOrderForDetails.createdAt) }}</div>
            </div>
          </div>

          <div class="order-info-card">
            <div class="info-icon"><i class="fas fa-clock"></i></div>
            <div class="info-content">
              <div class="info-label">Heure</div>
              <div class="info-value">{{ formatTime(selectedOrderForDetails.createdAt) }}</div>
            </div>
          </div>

          <div class="order-info-card">
            <div class="info-icon"><i class="fas fa-euro-sign"></i></div>
            <div class="info-content">
              <div class="info-label">Total</div>
              <div class="info-value">{{ parseFloat(selectedOrderForDetails.total).toFixed(2) }}€</div>
            </div>
          </div>
        </div>

        <div class="order-items-section">
          <h3><i class="fas fa-pizza-slice"></i> Pizzas commandées</h3>

          <div class="order-items-container">
            <div v-for="(pizzaItem, index) in selectedOrderForDetails.pizzaItems"
                 :key="index"
                 class="pizza-item-card">

              <!-- Modifiez cette partie dans votre template pour ajouter du débogage -->
              <div v-if="pizzaItem.pizzaDetails" class="pizza-item-content">
                <!-- Logs pour débogage (masqués en production) -->
                <pre v-if="false" style="background: #f5f5f5; padding: 10px; font-size: 12px; overflow: auto;">
    extraIngredients: {{ JSON.stringify(pizzaItem.extraIngredients) }}
    extraIngredientsDetails: {{ JSON.stringify(pizzaItem.extraIngredientsDetails) }}
  </pre>

                <div class="pizza-item-header">
                  <div class="pizza-name">{{ pizzaItem.pizzaDetails.name }}</div>
                  <div class="pizza-quantity">
                    <span class="quantity-badge">{{ pizzaItem.quantity }}</span>
                  </div>
                </div>



                <!-- Remplacer votre section d'affichage des suppléments par celle-ci -->
                <div v-if="pizzaItem.extraIngredientsDetails && pizzaItem.extraIngredientsDetails.length > 0"
                     class="supplements-section">
                  <div class="supplements-title">Suppléments:</div>
                  <div class="supplements-list">
                    <div v-for="(ingredient, idx) in pizzaItem.extraIngredientsDetails"
                         :key="idx"
                         class="supplement-item">
                      <div class="supplement-info">
                        <div class="supplement-name">{{ ingredient.name }}</div>
                        <div v-if="ingredient.quantity && ingredient.quantity > 1" class="supplement-quantity">
                          x{{ ingredient.quantity }}
                        </div>
                      </div>

                    </div>
                  </div>
                </div>

                <!-- Afficher un message si aucun supplément -->
                <div v-else-if="pizzaItem.extraIngredients && pizzaItem.extraIngredients.length > 0"
                     class="supplements-section">
                  <div class="supplements-title">Problème avec les suppléments</div>
                  <div class="supplements-error">
                    Des suppléments sont présents ({{ pizzaItem.extraIngredients.length }}) mais n'ont pas pu être affichés
                  </div>
                </div>
              </div>

              <div v-else class="pizza-error">
                <i class="fas fa-exclamation-triangle"></i>
                Détails de la pizza non disponibles
              </div>
            </div>

            <!-- Message si aucune pizza -->
            <div v-if="!selectedOrderForDetails.pizzaItems || selectedOrderForDetails.pizzaItems.length === 0"
                 class="no-items-message">
              <i class="fas fa-info-circle"></i> Aucune pizza dans cette commande
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button @click="closeOrderDetailsModal" class="btn-close">Fermer</button>
        <button v-if="selectedOrderForDetails.status !== 'Terminée'"
                @click="updateOrderStatus(selectedOrderForDetails)"
                class="btn-complete">
          <i class="fas fa-check"></i> Marquer comme terminée
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import Gestion_Ingredient from "@/components/Gestion_Ingredient.vue";
import Gestion_Pizza from "@/components/Gestion_Pizza.vue";
import OrderService from "@/services/OrderService";
import PizzaService from "@/services/PizzaService";
import IngredientService from "@/services/IngredientService";

export default {
  name: 'AdminView',
  components: {
    Gestion_Ingredient,
    Gestion_Pizza
  },
  data() {
    return {
      activeSection: '',
      todayOrders: [],
      loading: false,
      orderLoading: false,
      error: null,
      selectedOrderForDetails: null,
      showOrderDetailsModal: false
    }
  },
  computed: {
    ordersCount() {
      return this.todayOrders.length;
    },
    pendingOrdersCount() {
      return this.todayOrders.filter(order => order.status === 'En cours').length;
    },
    deliveredOrdersCount() {
      return this.todayOrders.filter(order => order.status === 'Terminée').length;
    },
    totalRevenue() {
      return this.todayOrders.reduce((sum, order) => {
        const total = typeof order.total === 'number' ? order.total :
            (parseFloat(order.total) || 0);
        return sum + total;
      }, 0).toFixed(2);
    },
    // Nouvelle propriété qui trie les commandes
    sortedOrders() {
      // Créer une copie pour ne pas modifier todayOrders directement
      return [...this.todayOrders].sort((a, b) => {
        // Si les statuts sont différents, trier par statut
        if (a.status !== b.status) {
          // 'En cours' vient avant 'Terminée'
          return a.status === 'Terminée' ? 1 : -1;
        }

        // Si les statuts sont identiques, trier par date/heure (plus récent d'abord)
        const dateA = new Date(a.createdAt || a.orderTime);
        const dateB = new Date(b.createdAt || b.orderTime);
        return dateA - dateB;
      });
    }
  },
  methods: {
    async fetchOrders() {
      this.loading = true;
      this.error = null;
      try {
        const orders = await OrderService.getUserOrders();
        // Filtrer pour n'avoir que les commandes du jour
        const today = new Date().toISOString().split('T')[0];
        this.todayOrders = orders.filter(order => {
          // Convertir la date de la commande au format ISO pour comparaison
          const orderDate = new Date(order.createdAt || order.orderTime).toISOString().split('T')[0];
          return orderDate === today;
        }).map(order => {
          // Normaliser les statuts
          if (order.status === 'Livrée' || order.status === 'Terminée') {
            order.status = 'Terminée';
          } else {
            // Tous les autres statuts sont considérés comme "En cours"
            order.status = 'En cours';
          }
          return order;
        });
      } catch (error) {
        console.error('Erreur lors de la récupération des commandes:', error);
        this.error = 'Impossible de charger les commandes. Veuillez réessayer plus tard.';
        this.todayOrders = [];
      } finally {
        this.loading = false;
      }
    },
    formatTime(date) {
      if (!date) return '';
      const dateObj = new Date(date);
      return dateObj.toLocaleTimeString('fr-FR', {hour: '2-digit', minute: '2-digit'});
    },
    formatDate(date) {
      if (!date) return '';
      const dateObj = new Date(date);
      return dateObj.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
    getStatusClass(status) {
      switch (status) {
        case 'En cours':
          return 'status-processing';
        case 'Terminée':
          return 'status-delivered';
        default:
          return '';
      }
    },
    async viewOrderDetails(order) {
      try {
        // Afficher un indicateur de chargement
        this.orderLoading = true;

        console.log('1. Début récupération des détails de la commande:', order.id);

        // Récupérer les détails complets de la commande
        const orderDetails = await OrderService.getOrderById(order.id);
        console.log('2. Détails de la commande reçus:', orderDetails);

        // Si la commande n'a pas de pizzaItems, initialiser un tableau vide
        if (!orderDetails.pizzaItems) {
          orderDetails.pizzaItems = [];
        }

        console.log('3. pizzaItems de la commande:', orderDetails.pizzaItems);

        // Traiter les pizzas et leurs suppléments
        if (orderDetails.pizzaItems && orderDetails.pizzaItems.length > 0) {
          // Récupérer tous les ingrédients en une seule fois (pour les suppléments)
          const allIngredients = await IngredientService.getAllIngredients();
          console.log('4. Tous les ingrédients disponibles:', allIngredients);

          // Créer un Map pour accès rapide par ID
          const ingredientsMap = new Map();
          allIngredients.forEach(ingredient => {
            ingredientsMap.set(ingredient.id, ingredient);
          });
          console.log('5. Map des ingrédients créé. Exemple d\'entrée:',
              allIngredients.length > 0 ?
                  `Pour l'ID ${allIngredients[0].id}, la valeur est:` : 'Aucun ingrédient disponible',
              allIngredients.length > 0 ? ingredientsMap.get(allIngredients[0].id) : null
          );

          // Enrichir chaque pizza avec ses détails
          const pizzaDetailsPromises = orderDetails.pizzaItems.map(async (pizzaItem, index) => {
            try {
              console.log(`6. Traitement de la pizza ${index} (ID: ${pizzaItem.pizzaId})`);

              // Récupérer les détails de la pizza
              const pizzaDetails = await PizzaService.getPizzaById(pizzaItem.pizzaId);
              console.log(`7. Détails de la pizza ${index} reçus:`, pizzaDetails);

              // Vérifier les ingrédients supplémentaires
              console.log(`8. Ingrédients supplémentaires bruts pour pizza ${index}:`, pizzaItem.extraIngredients);

              // Enrichir uniquement les ingrédients supplémentaires
              if (pizzaItem.extraIngredients && pizzaItem.extraIngredients.length > 0) {
                console.log(`9. Type du premier ingrédient supplémentaire:`, typeof pizzaItem.extraIngredients[0]);

                // Transformation en utilisant ingredientId pour rechercher les détails complets
                pizzaItem.extraIngredientsDetails = pizzaItem.extraIngredients.map(extraIngredient => {
                  // Récupérer l'ID de l'ingrédient (peut être stocké dans différentes propriétés)
                  const ingredientId = extraIngredient.ingredientId || extraIngredient.id || extraIngredient._id;

                  // Récupérer les détails complets depuis la Map
                  const ingredientDetails = ingredientsMap.get(ingredientId);
                  console.log(`Recherche de l'ingrédient ID ${ingredientId}:`, ingredientDetails);

                  if (ingredientDetails) {
                    // Fusionner les détails de l'ingrédient avec les propriétés spécifiques de l'extra
                    return {
                      // Détails de base de l'ingrédient (nom, prix, etc.)
                      ...ingredientDetails,
                      // Quantité depuis l'extra (si disponible)
                      quantity: extraIngredient.quantity || 1
                    };
                  } else {
                    // Fallback si l'ingrédient n'est pas trouvé dans la Map
                    return {
                      id: ingredientId,
                      name: extraIngredient.name || `Supplément #${ingredientId}`,
                      prix: extraIngredient.unitPrice || extraIngredient.prix || 0,
                      quantity: extraIngredient.quantity || 1
                    };
                  }
                });

                console.log('Ingrédient original:', pizzaItem.extraIngredients[0]);
                console.log('Ingrédient enrichi:', pizzaItem.extraIngredientsDetails[0]);
                console.log(`12. extraIngredientsDetails finaux pour pizza ${index}:`, pizzaItem.extraIngredientsDetails);
              } else {
                console.log(`9. Pas d'ingrédients supplémentaires pour pizza ${index}`);
                pizzaItem.extraIngredientsDetails = [];
              }

              return {
                ...pizzaItem,
                pizzaDetails: pizzaDetails
              };
            } catch (error) {
              console.error(`Erreur: impossible de récupérer les détails de la pizza ${pizzaItem.pizzaId}`, error);
              return pizzaItem;
            }
          });

          // Attendre que toutes les promesses soient résolues
          orderDetails.pizzaItems = await Promise.all(pizzaDetailsPromises);
          console.log('13. Toutes les pizzas avec détails enrichis:', orderDetails.pizzaItems);
        }

        // Afficher les détails
        this.selectedOrderForDetails = orderDetails;
        this.showOrderDetailsModal = true;

        // Un dernier log après que tout est affiché
        console.log('14. État final de selectedOrderForDetails:', this.selectedOrderForDetails);

      } catch (error) {
        console.error('Erreur lors de la récupération des détails de la commande:', error);
        alert('Erreur lors de la récupération des détails de la commande.');
      } finally {
        this.orderLoading = false;
      }
    },
    closeOrderDetailsModal() {
      this.showOrderDetailsModal = false;
      // Réinitialiser après une courte animation
      setTimeout(() => {
        this.selectedOrderForDetails = null;
      }, 300);
    },
    // Vous pouvez aussi améliorer la méthode updateOrderStatus pour garantir que le tri est respecté
    async updateOrderStatus(order) {
      try {
        if (order.status !== 'Terminée') {
          // Mettre à jour le statut de la commande à "Terminée"
          await OrderService.updateOrderStatus(order.id, 'Terminée');

          // Mettre à jour l'affichage local
          order.status = 'Terminée';

          // Si nous sommes dans le modal, mettre à jour la commande sélectionnée
          if (this.selectedOrderForDetails && this.selectedOrderForDetails.id === order.id) {
            this.selectedOrderForDetails.status = 'Terminée';
          }

          // Mettre à jour la liste des commandes
          const index = this.todayOrders.findIndex(o => o.id === order.id);
          if (index !== -1) {
            this.todayOrders[index].status = 'Terminée';
          }

          // Notifier l'utilisateur du succès
          this.$toast ?
              this.$toast.success('Commande marquée comme terminée avec succès.') :
              console.log('Commande marquée comme terminée:', order);

          // Forcer la mise à jour de l'interface - Le tri se fera automatiquement grâce à la propriété calculée
          this.$forceUpdate();
        }
      } catch (error) {
        console.error('Erreur lors de la mise à jour du statut de la commande:', error);
        this.$toast ?
            this.$toast.error('Erreur lors de la mise à jour du statut de la commande.') :
            alert('Erreur lors de la mise à jour du statut de la commande.');
      }
    }
  },
  mounted() {
    // Définir la section active par défaut
    this.activeSection = 'orders';
    // Charger les commandes au démarrage
    this.fetchOrders();
  },
  watch: {
    activeSection(newSection) {
      // Recharger les commandes quand l'utilisateur clique sur la section des commandes
      if (newSection === 'orders') {
        this.fetchOrders();
      }
    }
  }
}
</script>

<style scoped>
/* Styles pour le modal de détails de commande */
.order-details-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.4s ease;
}

@keyframes slideUp {
  from { transform: translateY(50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  background-color: #f8f8f8;
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
  display: flex;
  align-items: center;
  position: relative;
}

.modal-header h2 {
  margin: 0;
  color: #333;
  font-size: 1.6rem;
  font-weight: 700;
  flex: 1;
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  padding: 5px;
  color: #666;
  transition: all 0.2s;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  background-color: rgba(0, 0, 0, 0.1);
  color: #333;
}

.order-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  margin-right: 15px;
}

.status-processing {
  background-color: #3498db;
  color: white;
}

.status-delivered {
  background-color: #2ecc71;
  color: white;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  max-height: calc(90vh - 160px); /* Header + Footer height */
}

.order-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.order-info-card {
  background-color: #f9f9f9;
  border-radius: 10px;
  padding: 15px;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}

.order-info-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.info-icon {
  width: 36px;
  height: 36px;
  background-color: #e74c3c;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 0.8rem;
  color: #777;
  margin-bottom: 3px;
}

.info-value {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.order-items-section {
  margin-top: 20px;
}

.order-items-section h3 {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.order-items-section h3 i {
  margin-right: 10px;
  color: #e74c3c;
}

.order-items-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.pizza-item-card {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
  border: 1px solid #eee;
}

.pizza-item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.pizza-item-content {
  padding: 16px;
}

.pizza-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.pizza-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
}

.pizza-quantity {
  display: flex;
  align-items: center;
}

.quantity-badge {
  background-color: #e74c3c;
  color: white;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
}

.pizza-price {
  font-size: 1.1rem;
  color: #e74c3c;
  font-weight: 600;
  margin-bottom: 15px;
}

.pizza-total {
  font-size: 0.9rem;
  color: #777;
  font-weight: normal;
  margin-left: 5px;
}

.pizza-error {
  padding: 16px;
  color: #e74c3c;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
}

.pizza-error i {
  margin-right: 8px;
}

.no-items-message {
  padding: 30px;
  background-color: #f8f8f8;
  text-align: center;
  border-radius: 10px;
  color: #777;
  grid-column: 1 / -1;
}

.no-items-message i {
  margin-right: 8px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eaeaea;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-close,
.btn-complete {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close {
  background-color: #f0f0f0;
  color: #555;
}

.btn-close:hover {
  background-color: #e0e0e0;
}

.btn-complete {
  background-color: #2ecc71;
  color: white;
}

.btn-complete:hover {
  background-color: #27ae60;
}

.btn-complete i,
.btn-close i {
  margin-right: 8px;
}

/* Styles simplifiés pour les suppléments */
.supplements-section {
  margin-top: 15px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.supplements-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #e74c3c;
  margin-bottom: 10px;
}

.supplements-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.supplement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff5f5;
  border-radius: 6px;
  padding: 8px 12px;
}

.supplement-name {
  font-weight: 500;
  color: #333;
}

.supplement-price {
  font-weight: 600;
  color: #e74c3c;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .order-info-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  }

  .order-items-container {
    grid-template-columns: 1fr;
  }

  .pizza-name {
    font-size: 1.1rem;
  }

  .info-value {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .modal-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-status {
    margin-top: 10px;
    margin-right: 0;
  }

  .close-button {
    position: absolute;
    top: 15px;
    right: 15px;
  }

  .modal-footer {
    flex-direction: column-reverse;
  }

  .btn-close,
  .btn-complete {
    width: 100%;
  }
}
.admin-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.admin-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 0;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.admin-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.admin-subtitle {
  font-size: 18px;
  color: #666;
}

.admin-dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.admin-card {
  display: flex;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 5px solid transparent;
}

.admin-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.admin-card.active {
  border-left-color: #e74c3c;
  background-color: #fdf5f5;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-right: 15px;
  font-size: 24px;
  color: #e74c3c;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.card-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.admin-content {
  margin-top: 30px;
}

/* Styles pour la section des commandes */
.orders-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  border-top: 4px solid #e74c3c;
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

.error-message {
  text-align: center;
  padding: 20px;
  color: #e74c3c;
  background-color: #fdf5f5;
  border-radius: 8px;
  margin: 20px 0;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.orders-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #e74c3c;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.orders-table-container {
  overflow-x: auto;
}

.orders-table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th {
  text-align: center;
  padding: 12px;
  background-color: #f2f2f2;
  border-bottom: 2px solid #ddd;
  font-weight: 600;
}

.orders-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.orders-table tr.completed {
  background-color: #f8fff8;
}

.no-orders {
  text-align: center;
  color: #999;
  padding: 20px;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-processing {
  background-color: #3498db;
  color: white;
}

.status-delivered {
  background-color: #2ecc71;
  color: white;
}

.actions-cell {
  white-space: nowrap;
}

.btn-view,
.btn-status {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 12px;
  transition: background-color 0.2s;
  margin-right: 5px;
}

.btn-view {
  background-color: #f5f5f5;
  color: #333;
}

.btn-view:hover {
  background-color: #e0e0e0;
}

.btn-status {
  background-color: #2ecc71;
  color: white;
}

.btn-status:hover:not(:disabled) {
  background-color: #27ae60;
}

.btn-status:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-status-completed {
  background-color: #95a5a6;
}

@media (max-width: 768px) {
  .admin-dashboard {
    grid-template-columns: 1fr;
  }

  .actions-cell {
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .btn-view, .btn-status {
    width: 100%;
    margin-right: 0;
  }
}
</style>