<template>
  <div class="pizza-management">
    <h2 class="section-title" @click="toggleContent" :class="{ 'clickable': true, 'active': isContentVisible }">
      Gestion des Pizzas
      <span class="toggle-icon">{{ isContentVisible ? '▼' : '▶' }}</span>
    </h2>

    <div v-if="isContentVisible">
      <!-- Formulaire d'ajout/modification -->
      <div class="pizza-form">
        <h3>{{ isEditing ? 'Modifier la pizza' : 'Ajouter une nouvelle pizza' }}</h3>
        <div class="form-group" v-if="isEditing">
          <label for="id">ID</label>
          <label>{{currentPizza.id}}</label>
        </div>
        <div class="form-group">
          <label for="name">Nom</label>
          <input type="text" id="name" v-model="currentPizza.name" required>
        </div>

        <div class="form-group">
          <label for="composition">Composition</label>
          <textarea id="composition" v-model="currentPizza.composition" rows="3"></textarea>
        </div>

        <div class="form-group">
          <label for="price">Prix (€)</label>
          <input type="number" id="price" v-model="currentPizza.prix" step="0.01" min="0" required>
        </div>

        <div class="form-group">
          <label for="photo">URL Photo</label>
          <input type="text" id="photo" v-model="currentPizza.photo">
        </div>

        <div class="form-preview">
          <h4>Aperçu de l'image</h4>
          <div class="image-preview">
            <img v-if="currentPizza.photo" :src="currentPizza.photo" alt="Aperçu">
            <div v-else class="no-preview">Aucune image disponible</div>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-cancel" @click="resetForm">Annuler</button>
          <button class="btn-submit" @click="savePizza">{{ isEditing ? 'Mettre à jour' : 'Ajouter' }}</button>
        </div>
      </div>

      <!-- Liste des pizzas avec options de gestion -->
      <div class="pizzas-list">
        <h3>Liste des Pizzas</h3>
        <div class="search-bar">
          <input type="text" v-model="searchQuery" placeholder="Rechercher une pizza...">
        </div>

        <table>
          <thead>
          <tr>
            <th>Image</th>
            <th>Nom</th>
            <th>Composition</th>
            <th>Prix (€)</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="pizza in filteredPizzas" :key="pizza.id">
            <td class="image-cell">
              <img v-if="pizza.photo" :src="pizza.photo" :alt="pizza.name" class="thumbnail">
              <div v-else class="no-thumbnail">N/A</div>
            </td>
            <td>{{ pizza.name }}</td>
            <td class="composition-cell">{{ pizza.composition }}</td>
            <td>{{ pizza.prix }}</td>
            <td class="actions-cell">
              <button class="btn-edit" @click="editPizza(pizza)">Modifier</button>
              <button class="btn-delete" @click="confirmDelete(pizza)">Supprimer</button>
            </td>
          </tr>
          <tr v-if="filteredPizzas.length === 0">
            <td colspan="5" class="no-results">Aucune pizza trouvée</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div class="modal" v-if="showDeleteModal">
      <div class="modal-content">
        <h3>Confirmation de suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer la pizza "{{ pizzaToDelete.name }}" ?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteModal = false">Annuler</button>
          <button class="btn-confirm-delete" @click="deletePizza(pizzaToDelete.id)">Confirmer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PizzaService from '@/services/PizzaService'

export default {
  name: 'Gestion_Pizza',
  data() {
    return {
      pizzas: [],
      currentPizza: this.getEmptyPizza(),
      isEditing: false,
      editingId: null,
      searchQuery: '',
      showDeleteModal: false,
      pizzaToDelete: null,
      isContentVisible: false // État pour contrôler la visibilité du contenu
    }
  },
  computed: {
    filteredPizzas() {
      if (!this.searchQuery) {
        return this.pizzas;
      }

      const query = this.searchQuery.toLowerCase();
      return this.pizzas.filter(pizza => {
        return pizza.name.toLowerCase().includes(query) ||
            pizza.composition.toLowerCase().includes(query);
      });
    }
  },
  methods: {
    toggleContent() {
      this.isContentVisible = !this.isContentVisible;
      // Charger les données uniquement quand le contenu est visible pour la première fois
      if (this.isContentVisible && this.pizzas.length === 0) {
        this.fetchPizzas();
      }
    },
    getEmptyPizza() {
      return {
        name: "",
        composition: "",
        prix: "",
        photo: ""
      }
    },
    async fetchPizzas() {
      try {
        this.pizzas = await PizzaService.getAllPizzas();
      } catch (error) {
        console.error('Erreur lors de la récupération des pizzas:', error);
        alert('Erreur lors de la récupération des pizzas');
      }
    },
    resetForm() {
      this.currentPizza = this.getEmptyPizza();
      this.isEditing = false;
      this.editingId = null;
    },
    editPizza(pizza) {
      this.isEditing = true;
      this.editingId = pizza.id;
      console.log(this.editingId);
      // Créer une copie profonde pour éviter de modifier l'objet original
      this.currentPizza = JSON.parse(JSON.stringify(pizza));
      // Faire défiler vers le formulaire
      this.$el.querySelector('.pizza-form').scrollIntoView({ behavior: 'smooth' });
    },
    async savePizza() {
      try {
        if (!this.currentPizza.name || !this.currentPizza.prix) {
          alert('Veuillez remplir tous les champs obligatoires');
          return;
        }

        if (this.isEditing) {
          // Mise à jour d'une pizza existante
          await PizzaService.updatePizza(this.editingId, this.currentPizza);
          alert('Pizza mise à jour avec succès');
        } else {
          // Ajout d'une nouvelle pizza
          console.log(this.currentPizza.id);
          await PizzaService.createPizza(this.currentPizza);
          alert('Pizza ajoutée avec succès');
        }

        // Rafraîchir la liste et réinitialiser le formulaire
        this.fetchPizzas();
        this.resetForm();
      } catch (error) {
        console.error('Erreur lors de l\'enregistrement:', error);
        alert('Erreur lors de l\'enregistrement de la pizza');
      }
    },
    confirmDelete(pizza) {
      this.pizzaToDelete = pizza;
      this.showDeleteModal = true;
    },
    async deletePizza(id) {
      try {
        await PizzaService.deletePizza(id);
        this.showDeleteModal = false;
        this.pizzaToDelete = null;
        alert('Pizza supprimée avec succès');
        await this.fetchPizzas();
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        alert('Erreur lors de la suppression de la pizza');
      }
    }
  },
  mounted() {
    // Ne pas charger les pizzas automatiquement au chargement du composant
    // Les données seront chargées quand l'utilisateur cliquera sur le titre
  }
}
</script>

<style scoped>
.pizza-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.section-title.clickable {
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 8px 8px 0 0;
  transition: background-color 0.2s;
}

.section-title.clickable:hover {
  background-color: #e1e8f0;
}

.section-title.active {
  background-color: #3498db;
  color: white;
}

.toggle-icon {
  font-size: 14px;
  margin-left: 10px;
}

/* Formulaire */
.pizza-form {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-preview {
  margin-top: 20px;
}

.image-preview {
  width: 150px;
  height: 150px;
  border: 1px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #f5f5f5;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-preview {
  color: #999;
  font-style: italic;
  text-align: center;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  gap: 10px;
}

/* Liste des pizzas */
.pizzas-list {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-bar {
  margin-bottom: 15px;
}

.search-bar input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 12px;
  background-color: #f2f2f2;
  border-bottom: 2px solid #ddd;
}

td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.image-cell {
  width: 80px;
}

.thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.no-thumbnail {
  width: 60px;
  height: 60px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  color: #999;
  font-size: 12px;
}

.composition-cell {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.actions-cell {
  width: 200px;
  text-align: right;
}

.no-results {
  text-align: center;
  color: #999;
  padding: 20px;
}

/* Boutons */
.btn-submit,
.btn-cancel,
.btn-edit,
.btn-delete,
.btn-confirm-delete {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-submit {
  background-color: #3498db;
  color: white;
}

.btn-submit:hover {
  background-color: #2980b9;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #333;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-edit {
  background-color: #f39c12;
  color: white;
  margin-right: 5px;
}

.btn-edit:hover {
  background-color: #e67e22;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background-color: #c0392b;
}

.btn-confirm-delete {
  background-color: #e74c3c;
  color: white;
}

.btn-confirm-delete:hover {
  background-color: #c0392b;
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

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  gap: 10px;
}
</style>