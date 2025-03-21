<template>
  <div class="comments-section">
    <h3>Avis des clients ({{ comments.length }})</h3>

    <!-- Affichage de la note moyenne -->
    <div class="average-rating" v-if="averageRating">
      <div class="rating-value">{{ averageRating.toFixed(1) }}</div>
      <div class="rating-stars">
        <span v-for="i in 5" :key="i" class="star" :class="{ 'active': i <= Math.round(averageRating) }">★</span>
      </div>
      <div class="rating-count">{{ comments.length }} avis</div>
    </div>

    <!-- Formulaire d'ajout de commentaire -->
    <div v-if="isLoggedIn" class="comment-form">
      <h4>Laissez votre avis</h4>
      <div class="rating-selector">
        <span>Note :</span>
        <div class="stars">
          <span
              v-for="star in 5"
              :key="star"
              class="star"
              :class="{ 'active': star <= newComment.rating }"
              @click="newComment.rating = star"
          >★</span>
        </div>
      </div>
      <textarea
          v-model="newComment.content"
          placeholder="Partagez votre expérience..."
          rows="3"
          maxlength="1000"
      ></textarea>
      <div class="form-actions">
        <button
            @click="submitComment"
            class="btn-submit"
        >
          Publier
        </button>
      </div>

    </div>
    <div v-else class="login-prompt">
      <router-link to="/login" class="login-link">Connectez-vous</router-link> pour laisser un avis
    </div>

    <!-- Liste des commentaires -->
    <div v-if="loading" class="loading-comments">
      Chargement des avis...
    </div>
    <div v-else-if="comments.length === 0" class="no-comments">
      Aucun avis pour le moment. Soyez le premier à donner votre avis !
    </div>
    <div v-else class="comments-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="comment-user">{{ comment.userName || 'Utilisateur' }}</div>
          <div class="comment-date">{{ formatDate(comment.createdAt) }}</div>
        </div>
        <div class="comment-rating">
          <span v-for="star in 5" :key="star" class="star" :class="{ 'active': star <= comment.rating }">★</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div v-if="canManageComment(comment)" class="comment-actions">
          <button @click="editComment(comment)" class="btn-edit">Modifier</button>
          <button @click="confirmDeleteComment(comment.id)" class="btn-delete">Supprimer</button>
        </div>
      </div>
    </div>

    <!-- Modal de modification de commentaire -->
    <div v-if="showEditModal" class="edit-modal">
      <div class="modal-content">
        <h3>Modifier votre avis</h3>
        <div class="rating-selector">
          <span>Note :</span>
          <div class="stars">
            <span
                v-for="star in 5"
                :key="star"
                class="star"
                :class="{ 'active': star <= editingComment.rating }"
                @click="editingComment.rating = star"
            >★</span>
          </div>
        </div>
        <textarea
            v-model="editingComment.content"
            rows="3"
            maxlength="1000"
        ></textarea>
        <div class="modal-actions">
          <button @click="cancelEdit" class="btn-cancel">Annuler</button>
          <button @click="updateComment" class="btn-confirm" :disabled="isUpdating">
            {{ isUpdating ? 'Enregistrement...' : 'Enregistrer' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de confirmation de suppression -->
    <div v-if="showDeleteModal" class="delete-modal">
      <div class="modal-content">
        <h3>Confirmer la suppression</h3>
        <p>Êtes-vous sûr de vouloir supprimer cet avis ?</p>
        <div class="modal-actions">
          <button @click="cancelDelete" class="btn-cancel">Annuler</button>
          <button @click="deleteComment" class="btn-confirm-delete" :disabled="isDeleting">
            {{ isDeleting ? 'Suppression...' : 'Supprimer' }}
          </button>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import CommentService from '@/services/CommentService';
import AuthService from '@/services/AuthService';
import { ref, reactive, onMounted, computed } from 'vue';

export default {
  name: 'PizzaComments',
  props: {
    pizzaId: {
      type: Number,
      required: true
    }
  },
  setup(props) {
    const comments = ref([]);
    const loading = ref(true);
    const averageRating = ref(0);

    const isLoggedIn = computed(() => AuthService.isLoggedIn());
    const isAdmin = computed(() => AuthService.isAdmin());
    const currentUserId = ref(null);

    // État pour l'ajout de commentaire
    const newComment = reactive({
      pizzaId: props.pizzaId,
      content: '',
      rating: 5
    });
    const isSubmitting = ref(false);

    // État pour la modification de commentaire
    const showEditModal = ref(false);
    const editingComment = reactive({
      id: null,
      content: '',
      rating: 5
    });
    const isUpdating = ref(false);

    // État pour la suppression de commentaire
    const showDeleteModal = ref(false);
    const commentToDeleteId = ref(null);
    const isDeleting = ref(false);

    const canSubmit = computed(() => {
      console.log('Content:', newComment.content);
      console.log('Content trimmed:', newComment.content.trim());
      console.log('Rating:', newComment.rating);

      const result = newComment.content.trim() !== '' &&
          newComment.rating >= 1 &&
          newComment.rating <= 5;

      console.log('Can submit:', result);
      return result;
    });
    console.log(props.pizzaId);
    // Charger les commentaires et la note moyenne
    const loadComments = async () => {
      try {
        loading.value = true;

        // Charger les commentaires
        const commentData = await CommentService.getCommentsByPizzaId(props.pizzaId);
        comments.value = commentData;

        // Charger la note moyenne
        const ratingData = await CommentService.getAverageRatingForPizza(props.pizzaId);
        averageRating.value = ratingData.averageRating;

        // Charger les informations de l'utilisateur connecté si disponible
        if (isLoggedIn.value) {
          try {
            const userInfo = await AuthService.getCurrentUser();
            currentUserId.value = userInfo.userId;
          } catch (error) {
            console.error('Erreur lors de la récupération des informations utilisateur:', error);
          }
        }
      } catch (error) {
        console.error('Erreur lors du chargement des commentaires:', error);
      } finally {
        loading.value = false;
      }
    };

    // Ajouter un commentaire
    const submitComment = async () => {
      if (!canSubmit.value) return;

      try {
        isSubmitting.value = true;

        await CommentService.createComment({
          pizzaId: props.pizzaId,
          content: newComment.content,
          rating: newComment.rating
        });

        // Réinitialiser le formulaire
        newComment.content = '';
        newComment.rating = 5;

        // Recharger les commentaires
        await loadComments();
      } catch (error) {
        console.error('Erreur lors de l\'ajout du commentaire:', error);
      } finally {
        isSubmitting.value = false;
      }
    };

    // Vérifier si l'utilisateur peut gérer ce commentaire
    const canManageComment = (comment) => {
      return isLoggedIn.value && (
          isAdmin.value ||
          (currentUserId.value && comment.userId === currentUserId.value)
      );
    };

    // Ouvrir le modal d'édition
    const editComment = (comment) => {
      editingComment.id = comment.id;
      editingComment.content = comment.content;
      editingComment.rating = comment.rating;
      showEditModal.value = true;
    };

    // Annuler l'édition
    const cancelEdit = () => {
      showEditModal.value = false;
    };

    // Mettre à jour un commentaire
    const updateComment = async () => {
      try {
        isUpdating.value = true;

        await CommentService.updateComment(editingComment.id, {
          content: editingComment.content,
          rating: editingComment.rating
        });

        // Fermer le modal
        showEditModal.value = false;

        // Recharger les commentaires
        await loadComments();
      } catch (error) {
        console.error('Erreur lors de la mise à jour du commentaire:', error);
      } finally {
        isUpdating.value = false;
      }
    };

    // Ouvrir le modal de confirmation de suppression
    const confirmDeleteComment = (commentId) => {
      commentToDeleteId.value = commentId;
      showDeleteModal.value = true;
    };

    // Annuler la suppression
    const cancelDelete = () => {
      showDeleteModal.value = false;
      commentToDeleteId.value = null;
    };

    // Supprimer un commentaire
    const deleteComment = async () => {
      try {
        isDeleting.value = true;

        await CommentService.deleteComment(commentToDeleteId.value);

        // Fermer le modal
        showDeleteModal.value = false;
        commentToDeleteId.value = null;

        // Recharger les commentaires
        await loadComments();
      } catch (error) {
        console.error('Erreur lors de la suppression du commentaire:', error);
      } finally {
        isDeleting.value = false;
      }
    };

    // Formater la date
    const formatDate = (dateString) => {
      if (!dateString) return '';

      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    };

    onMounted(() => {
      loadComments();
    });

    return {
      comments,
      loading,
      averageRating,
      isLoggedIn,
      newComment,
      isSubmitting,
      canSubmit,
      submitComment,
      canManageComment,
      editComment,
      showEditModal,
      editingComment,
      cancelEdit,
      updateComment,
      isUpdating,
      confirmDeleteComment,
      showDeleteModal,
      cancelDelete,
      deleteComment,
      isDeleting,
      formatDate
    };
  }
}
</script>

<style scoped>
.comments-section {
  margin-top: 30px;
}

.comments-section h3 {
  font-size: 22px;
  color: #333;
  margin-bottom: 20px;
}

.average-rating {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.rating-value {
  font-size: 30px;
  font-weight: bold;
  color: #e74c3c;
  margin-right: 15px;
}

.rating-stars {
  display: flex;
  margin-right: 15px;
}

.rating-count {
  color: #666;
  font-size: 14px;
}

.comment-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  min-height: 250px; /* Ajoutez une hauteur minimale */
  display: flex;
  flex-direction: column; /* Assurez une disposition verticale */
  border : solid 1px red;
}

.comment-form h4 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.rating-selector {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating-selector span {
  margin-right: 10px;
  font-weight: 500;
}

.stars {
  display: flex;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star.active {
  color: #f39c12;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
  font-size: 14px;
  margin-bottom: 15px;
}

.form-actions {
  margin-top: auto; /* Poussera le bouton vers le bas */
  width: 100%; /* S'assure que le bouton prend toute la largeur */
}

.btn-submit {
  width: 100%; /* Bouton qui prend toute la largeur */
  padding: 10px 20px;
  margin-top: 10px; /* Espace au-dessus du bouton */
}

.btn-submit:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-submit:disabled {
  background-color: #bbb;
  cursor: not-allowed;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 30px;
}

.login-link {
  color: #e74c3c;
  text-decoration: none;
  font-weight: 500;
}

.login-link:hover {
  text-decoration: underline;
}

.loading-comments {
  text-align: center;
  padding: 20px;
  color: #666;
}

.no-comments {
  text-align: center;
  padding: 20px;
  color: #666;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-user {
  font-weight: 600;
  color: #333;
}

.comment-date {
  color: #999;
  font-size: 14px;
}

.comment-rating {
  margin-bottom: 10px;
}

.comment-content {
  color: #555;
  line-height: 1.5;
  margin-bottom: 15px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-edit,
.btn-delete {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-edit {
  background-color: #f2f2f2;
  color: #333;
}

.btn-edit:hover {
  background-color: #e0e0e0;
}

.btn-delete {
  background-color: #f9e5e5;
  color: #e74c3c;
}

.btn-delete:hover {
  background-color: #f8d7d7;
}

/* Styles pour les modals */
.edit-modal,
.delete-modal {
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
  width: 500px;
  max-width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
  margin-bottom: 15px;
  color: #333;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel,
.btn-confirm,
.btn-confirm-delete {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-cancel {
  background-color: #f2f2f2;
  color: #333;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-confirm {
  background-color: #e74c3c;
  color: white;
}

.btn-confirm:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-confirm-delete {
  background-color: #e74c3c;
  color: white;
}

.btn-confirm-delete:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-confirm:disabled,
.btn-confirm-delete:disabled {
  background-color: #bbb;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .comment-date {
    margin-top: 5px;
  }

  .comment-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-edit,
  .btn-delete {
    width: 100%;
    text-align: center;
    margin-bottom: 5px;
  }
  .average-rating {
    flex-direction: column;
    align-items: flex-start;
  }
  .rating-value {
    margin-bottom: 5px;
  }
  .rating-stars {
    margin-bottom: 5px;
  }
}
</style>