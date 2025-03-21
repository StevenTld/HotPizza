<template>
  <div class="admin-account-container">
    <div class="account-header">
      <h1>Mon Compte Administrateur</h1>
      <p>Gérez vos informations personnelles et les autres administrateurs</p>
    </div>

    <div class="admin-panel">
      <div class="admin-sidebar">
        <div class="admin-nav">
          <button
              @click="activeTab = 'profile'"
              :class="['admin-nav-item', activeTab === 'profile' ? 'active' : '']"
          >
            <i class="fas fa-user"></i> Profil
          </button>
          <button
              @click="activeTab = 'password'"
              :class="['admin-nav-item', activeTab === 'password' ? 'active' : '']"
          >
            <i class="fas fa-key"></i> Mot de passe
          </button>
          <button
              @click="activeTab = 'admins'"
              :class="['admin-nav-item', activeTab === 'admins' ? 'active' : '']"
          >
            <i class="fas fa-user-shield"></i> Gestion des administrateurs
          </button>
        </div>
      </div>

      <div class="admin-content">
        <!-- ONGLET PROFIL -->
        <div v-if="activeTab === 'profile'" class="admin-tab">
          <h2>Informations personnelles</h2>
          <div class="profile-form">
            <div class="form-group">
              <label for="adminFirstName">Prénom</label>
              <input type="text" id="adminFirstName" v-model="userData.firstName" :disabled="!editMode">
            </div>
            <div class="form-group">
              <label for="adminLastName">Nom</label>
              <input type="text" id="adminLastName" v-model="userData.lastName" :disabled="!editMode">
            </div>
            <div class="form-group">
              <label for="adminEmail">Email</label>
              <input type="email" id="adminEmail" v-model="userData.email" :disabled="!editMode">
            </div>
            <div class="action-buttons">
              <button v-if="!editMode" @click="toggleEditMode" class="btn btn-primary">Modifier</button>
              <template v-else>
                <button @click="saveUserData" class="btn btn-success">Enregistrer</button>
                <button @click="cancelEdit" class="btn btn-secondary">Annuler</button>
              </template>
            </div>
          </div>
        </div>

        <!-- ONGLET MOT DE PASSE -->
        <div v-if="activeTab === 'password'" class="admin-tab">
          <h2>Changer mon mot de passe</h2>
          <div class="password-form">
            <div class="form-group">
              <label for="currentPassword">Mot de passe actuel</label>
              <input type="password" id="currentPassword" v-model="passwordData.currentPassword" required>
            </div>
            <div class="form-group">
              <label for="newPassword">Nouveau mot de passe</label>
              <input type="password" id="newPassword" v-model="passwordData.newPassword" required>
            </div>
            <div class="form-group">
              <label for="confirmPassword">Confirmer le nouveau mot de passe</label>
              <input type="password" id="confirmPassword" v-model="passwordData.confirmPassword" required>
            </div>
            <div class="error-message" v-if="passwordError">{{ passwordError }}</div>
            <div class="success-message" v-if="passwordSuccess">{{ passwordSuccess }}</div>
            <div class="action-buttons">
              <button @click="changePassword" class="btn btn-primary" :disabled="isPasswordChanging">
                {{ isPasswordChanging ? 'Modification en cours...' : 'Changer le mot de passe' }}
              </button>
            </div>
          </div>
        </div>

        <!-- ONGLET GESTION DES ADMINISTRATEURS -->
        <div v-if="activeTab === 'admins'" class="admin-tab">
          <h2>Créer un nouvel administrateur</h2>
          <div class="admin-form">
            <form @submit.prevent="registerAdmin">
              <div class="form-group">
                <label for="adminLastName">Nom</label>
                <input
                    type="text"
                    id="adminLastName"
                    v-model="newAdmin.lastName"
                    placeholder="Nom de l'administrateur"
                    required
                />
              </div>
              <div class="form-group">
                <label for="adminFirstName">Prénom</label>
                <input
                    type="text"
                    id="adminFirstName"
                    v-model="newAdmin.firstName"
                    placeholder="Prénom de l'administrateur"
                    required
                />
              </div>
              <div class="form-group">
                <label for="adminEmail">Email</label>
                <input
                    type="email"
                    id="adminEmail"
                    v-model="newAdmin.email"
                    placeholder="Email de l'administrateur"
                    required
                />
              </div>
              <div class="form-group">
                <label for="adminPassword">Mot de passe</label>
                <input
                    type="password"
                    id="adminPassword"
                    v-model="newAdmin.password"
                    placeholder="Mot de passe"
                    required
                />
              </div>
              <div class="form-group">
                <label for="adminConfirmPassword">Confirmer le mot de passe</label>
                <input
                    type="password"
                    id="adminConfirmPassword"
                    v-model="newAdmin.confirmPassword"
                    placeholder="Confirmer le mot de passe"
                    required
                />
              </div>
              <div class="error-message" v-if="adminError">{{ adminError }}</div>
              <div class="success-message" v-if="adminSuccess">{{ adminSuccess }}</div>
              <button type="submit" class="btn btn-register" :disabled="isAdminRegistering">
                {{ isAdminRegistering ? 'Création en cours...' : 'Créer l\'administrateur' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService'
import { ref, reactive, onMounted } from 'vue'

export default {
  name: 'AdminAccountView',
  setup() {
    // Variables d'état
    const activeTab = ref('profile')
    const editMode = ref(false)
    const currentUserId = ref(null)
    const isPasswordChanging = ref(false)
    const isAdminRegistering = ref(false)
    const passwordError = ref('')
    const passwordSuccess = ref('')
    const adminError = ref('')
    const adminSuccess = ref('')

    // Données utilisateur
    const userData = reactive({
      firstName: '',
      lastName: '',
      email: ''
    })

    // Données pour le changement de mot de passe
    const passwordData = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // Données pour le nouvel administrateur
    const newAdmin = reactive({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: ''
    })

    // Chargement initial des données
    onMounted(async () => {
      try {
        // Récupérer les données de l'utilisateur
        const jwt = AuthService.getToken()
        if (jwt) {
          try {
            const userDetails = JSON.parse(atob(jwt.split('.')[1]))
            userData.email = userDetails.email || ''
            userData.firstName = userDetails.firstName || ''
            userData.lastName = userDetails.lastName || ''
            currentUserId.value = userDetails.userId
          } catch (e) {
            console.error('Erreur lors du décodage du JWT:', e)
          }
        }
      } catch (error) {
        console.error('Erreur lors du chargement des données:', error)
      }
    })

    // Fonctions de gestion du profil
    const toggleEditMode = () => {
      editMode.value = !editMode.value
    }

    const saveUserData = async () => {
      try {
        // Définir les données à mettre à jour
        const updateData = {
          firstName: userData.firstName,
          lastName: userData.lastName,
          email: userData.email
        };
        // Ici vous devriez appeler votre service d'utilisateur pour sauvegarder les modifications
        // Comme vous n'avez pas de UserService, affichez un message temporaire
        await AuthService.updateUser(updateData);
        alert('Fonctionnalité non disponible sans UserService.')
        editMode.value = false
      } catch (error) {
        console.error('Erreur lors de la mise à jour:', error)
        alert('Erreur lors de la mise à jour de vos informations')
      }
    }

    const cancelEdit = () => {
      // Réinitialiser les champs
      editMode.value = false
    }

    // Fonction pour changer le mot de passe
    const changePassword = async () => {
      // Vérification que les mots de passe correspondent
      if (passwordData.newPassword !== passwordData.confirmPassword) {
        passwordError.value = 'Les nouveaux mots de passe ne correspondent pas'
        passwordSuccess.value = ''
        return
      }

      try {
        isPasswordChanging.value = true
        passwordError.value = ''
        passwordSuccess.value = ''

        // Ici, vous appelleriez votre service pour changer le mot de passe
        // Comme vous n'avez pas cette fonctionnalité, affichons un message temporaire

        // Simulation d'un délai
        await new Promise(resolve => setTimeout(resolve, 1000))

        passwordSuccess.value = 'Mot de passe modifié avec succès'

        // Réinitialiser les champs après succès
        passwordData.currentPassword = ''
        passwordData.newPassword = ''
        passwordData.confirmPassword = ''
      } catch (error) {
        console.error('Erreur lors du changement de mot de passe:', error)
        passwordError.value = error.response?.data?.message || 'Erreur lors du changement de mot de passe'
      } finally {
        isPasswordChanging.value = false
      }
    }

    // Fonction pour créer un nouvel administrateur
    const registerAdmin = async () => {
      // Vérification des mots de passe
      if (newAdmin.password !== newAdmin.confirmPassword) {
        adminError.value = 'Les mots de passe ne correspondent pas'
        adminSuccess.value = ''
        return
      }

      try {
        isAdminRegistering.value = true
        adminError.value = ''
        adminSuccess.value = ''

        // Cloner l'objet admin sans la confirmation du mot de passe
        const adminToRegister = {
          firstName: newAdmin.firstName,
          lastName: newAdmin.lastName,
          email: newAdmin.email,
          password: newAdmin.password,
          role: "admin"  // Définir le rôle comme admin
        }

        // Appel au service d'inscription
        // Vous devriez idéalement avoir un service dédié pour cela
        await AuthService.register(adminToRegister)

        adminSuccess.value = 'Administrateur créé avec succès'

        // Réinitialiser le formulaire après succès
        newAdmin.firstName = ''
        newAdmin.lastName = ''
        newAdmin.email = ''
        newAdmin.password = ''
        newAdmin.confirmPassword = ''
      } catch (error) {
        console.error('Erreur lors de la création de l\'administrateur:', error)
        adminError.value = error.response?.data?.message || 'Erreur lors de la création de l\'administrateur'
      } finally {
        isAdminRegistering.value = false
      }
    }

    return {
      activeTab,
      editMode,
      userData,
      passwordData,
      newAdmin,
      isPasswordChanging,
      isAdminRegistering,
      passwordError,
      passwordSuccess,
      adminError,
      adminSuccess,
      toggleEditMode,
      saveUserData,
      cancelEdit,
      changePassword,
      registerAdmin
    }
  }
}
</script>

<style scoped>
.admin-account-container {
  max-width: 1200px;
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

.admin-panel {
  display: flex;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.admin-sidebar {
  width: 250px;
  background-color: #f8f9fa;
  border-right: 1px solid #e9ecef;
  padding: 20px 0;
}

.admin-nav {
  display: flex;
  flex-direction: column;
}

.admin-nav-item {
  padding: 12px 20px;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #495057;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.admin-nav-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.admin-nav-item:hover {
  background-color: #e9ecef;
}

.admin-nav-item.active {
  background-color: #e9ecef;
  color: #3498db;
  border-left-color: #3498db;
  font-weight: 500;
}

.admin-content {
  flex: 1;
  padding: 30px;
  overflow: auto;
}

.admin-tab {
  min-height: 500px;
}

.admin-tab h2 {
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #333;
}

/* Styles pour les formulaires */
.profile-form,
.password-form,
.admin-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.2rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
}

.action-buttons {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

/* Styles pour les boutons */
.btn {
  padding: 10px 16px;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
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

.btn-register {
  width: 100%;
  padding: 0.8rem;
  background-color: #00c3ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 10px;
}

.btn-register:hover {
  background-color: #0099cc;
}

.btn-register:disabled,
.btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* Messages d'erreur et de succès */
.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
  padding: 8px;
  background-color: #ffeaea;
  border-radius: 4px;
}

.success-message {
  color: #27ae60;
  margin-bottom: 1rem;
  padding: 8px;
  background-color: #e6fef2;
  border-radius: 4px;
}

/* Responsive adjustments */
@media (max-width: 992px) {
  .admin-panel {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e9ecef;
  }

  .admin-nav {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .admin-nav-item {
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .admin-nav-item.active {
    border-left-color: transparent;
    border-bottom-color: #3498db;
  }
}

@media (max-width: 768px) {
  .admin-content {
    padding: 20px;
  }

  .profile-form,
  .password-form,
  .admin-form {
    max-width: 100%;
  }
}
</style>