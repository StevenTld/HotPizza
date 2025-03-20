<template>
  <div class="ingredient-management">
    <h2 class="section-title" @click="toggleContent" :class="{ 'clickable': true, 'active': isContentVisible }">
      Gestion des Ingrédients
      <span class="toggle-icon">{{ isContentVisible ? '▼' : '▶' }}</span>
    </h2>

    <div v-if="isContentVisible">
      <!-- Formulaire d'ajout/modification -->
      <div class="ingredient-form">
        <h3>{{ isEditing ? 'Modifier l\'ingrédient' : 'Ajouter un nouvel ingrédient' }}</h3>
        <div class="form-group" v-if="isEditing">
          <label for="id">ID</label>
          <label>{{currentIngredient.id}}</label>
        </div>
        <div class="form-group">
          <label for="name">Nom</label>
          <input type="text" id="name" v-model="currentIngredient.name" required>
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea id="description" v-model="currentIngredient.description" rows="3"></textarea>
        </div>

        <div class="form-group">
          <label for="price">Prix (€)</label>
          <input type="number" id="price" v-model="currentIngredient.prix" step="0.01" min="0" required>
        </div>

        <div class="form-group">
          <label for="photo">URL Photo</label>
          <input type="text" id="photo" v-model="currentIngredient.photo">
        </div>

        <div class="form-preview">
          <h4>Aperçu de l'image</h4>
          <div class="image-preview">
            <img v-if="currentIngredient.photo" :src="currentIngredient.photo" alt="Aperçu">
            <div v-else class="no-preview">Aucune image disponible</div>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-cancel" @click="resetForm">Annuler</button>
          <button class="btn-submit" @click="saveIngredient">{{ isEditing ? 'Mettre à jour' : 'Ajouter' }}</button>
        </div>
      </div>

      <!-- Liste des ingrédients avec options de gestion -->
      <div class="ingredients-list">
        <h3>Liste des Ingrédients</h3>
        <div class="search-bar">
          <input type="text" v-model="searchQuery" placeholder="Rechercher un ingrédient...">
        </div>

        <table>
          <thead>
          <tr>
            <th>Image</th>
            <th>Nom</th>
            <th>Description</th>
            <th>Prix (€)</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="ingredient in filteredIngredients" :key="ingredient.id">
            <td class="image-cell">
              <img v-if="ingredient.photo" :src="ingredient.photo" :alt="ingredient.name" class="thumbnail">
              <div v-else class="no-thumbnail">N/A</div>
            </td>
            <td>{{ ingredient.name }}</td>
            <td class="description-cell">{{ ingredient.description }}</td>
            <td>{{ ingredient.prix }}</td>
            <td class="actions-cell">
              <button class="btn-edit" @click="editIngredient(ingredient)">Modifier</button>
              <button class="btn-delete" @click="confirmDelete(ingredient)">Supprimer</button>
            </td>
          </tr>
          <tr v-if="filteredIngredients.length === 0">
            <td colspan="5" class="no-results">Aucun ingrédient trouvé</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div class="modal" v-if="showDeleteModal">
      <div class="modal-content">
        <h3>Confirmation de suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer l'ingrédient "{{ ingredientToDelete.name }}" ?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteModal = false">Annuler</button>
          <button class="btn-confirm-delete" @click="deleteIngredient(ingredientToDelete.id)">Confirmer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import IngredientService from '@/services/IngredientService'

export default {
  name: 'Gestion_Ingredient',
  data() {
    return {
      ingredients: [],
      currentIngredient: this.getEmptyIngredient(),
      isEditing: false,
      editingId: null,
      searchQuery: '',
      showDeleteModal: false,
      ingredientToDelete: null,
      isContentVisible: false // État pour contrôler la visibilité du contenu
    }
  },
  computed: {
    filteredIngredients() {
      if (!this.searchQuery) {
        return this.ingredients;
      }

      const query = this.searchQuery.toLowerCase();
      return this.ingredients.filter(ingredient => {
        return ingredient.name.toLowerCase().includes(query) ||
            ingredient.description.toLowerCase().includes(query);
      });
    }
  },
  methods: {
    toggleContent() {
      this.isContentVisible = !this.isContentVisible;
      // Charger les données uniquement quand le contenu est visible pour la première fois
      if (this.isContentVisible && this.ingredients.length === 0) {
        this.fetchIngredients();
      }
    },
    getEmptyIngredient() {
      return {
        name: "",
        description: "",
        prix: "",
        photo: ""
      }
    },
    async fetchIngredients() {
      try {
        this.ingredients = await IngredientService.getAllIngredients();
      } catch (error) {
        console.error('Erreur lors de la récupération des ingrédients:', error);
        alert('Erreur lors de la récupération des ingrédients');
      }
    },
    resetForm() {
      this.currentIngredient = this.getEmptyIngredient();
      this.isEditing = false;
      this.editingId = null;
    },
    editIngredient(ingredient) {
      this.isEditing = true;
      this.editingId = ingredient.id;
      console.log(this.editingId);
      // Créer une copie profonde pour éviter de modifier l'objet original
      this.currentIngredient = JSON.parse(JSON.stringify(ingredient));
      // Faire défiler vers le formulaire
      this.$el.querySelector('.ingredient-form').scrollIntoView({ behavior: 'smooth' });
    },
    async saveIngredient() {
      try {
        if (!this.currentIngredient.name || !this.currentIngredient.prix) {
          alert('Veuillez remplir tous les champs obligatoires');
          return;
        }

        if (this.isEditing) {
          // Mise à jour d'un ingrédient existant
          await IngredientService.updateIngredient(this.editingId, this.currentIngredient);
          alert('Ingrédient mis à jour avec succès');
        } else {
          // Ajout d'un nouvel ingrédient
          console.log(this.currentIngredient.id);
          await IngredientService.createIngredient(this.currentIngredient);
          alert('Ingrédient ajouté avec succès');
        }

        // Rafraîchir la liste et réinitialiser le formulaire
        this.fetchIngredients();
        this.resetForm();
      } catch (error) {
        console.error('Erreur lors de l\'enregistrement:', error);
        alert('Erreur lors de l\'enregistrement de l\'ingrédient');
      }
    },
    confirmDelete(ingredient) {
      this.ingredientToDelete = ingredient;
      this.showDeleteModal = true;
    },
    async deleteIngredient(id) {
      try {
        await IngredientService.deleteIngredient(id);
        this.showDeleteModal = false;
        this.ingredientToDelete = null;
        alert('Ingrédient supprimé avec succès');
        await this.fetchIngredients();
      } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        alert('Erreur lors de la suppression de l\'ingrédient');
      }
    }
  },
  mounted() {
    // Ne pas charger les ingrédients automatiquement au chargement du composant
    // Les données seront chargées quand l'utilisateur cliquera sur le titre
  }
}
</script>

<style scoped>
.ingredient-management {
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
.ingredient-form {
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

/* Liste des ingrédients */
.ingredients-list {
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

.description-cell {
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