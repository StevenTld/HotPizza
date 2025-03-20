<template>
  <div class="register-container">
    <div class="register-form">
      <h2>Créer un compte</h2>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="lastName">Nom</label>
          <input
              type="text"
              id="name"
              v-model="user.lastName"
              placeholder="Votre nom"
              required
          />
        </div>
        <div class="form-group">
          <label for="firstName">Prénom</label>
          <input
              type="text"
              id="name"
              v-model="user.firstName"
              placeholder="Votre prénom"
              required
          />
        </div>
        <div class="form-group">
          <label for="email">Email</label>
          <input
              type="email"
              id="email"
              v-model="user.email"
              placeholder="Votre email"
              required
          />
        </div>
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              type="password"
              id="password"
              v-model="user.password"
              placeholder="Votre mot de passe"
              required
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">Confirmer le mot de passe</label>
          <input
              type="password"
              id="confirmPassword"
              v-model="user.confirmPassword"
              placeholder="Confirmer le mot de passe"
              required
          />
        </div>
        <div class="error-message" v-if="errorMessage">{{ errorMessage }}</div>
        <button type="submit" class="btn-register" :disabled="loading">
          {{ loading ? 'Inscription en cours...' : "S'inscrire" }}
        </button>
      </form>
      <div class="login-link">
        <p>Déjà un compte ? <a href="#" @click.prevent="goToLogin">Se connecter</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService'

export default {
  name: 'RegisterView',
  data() {
    return {
      user: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      errorMessage: '',
      loading: false
    }
  },
  methods: {
    async register() {
      // Vérification des mots de passe
      if (this.user.password !== this.user.confirmPassword) {
        this.errorMessage = 'Les mots de passe ne correspondent pas';
        return;
      }

      try {
        this.loading = true;
        this.errorMessage = '';

        // Cloner l'objet user sans la confirmation du mot de passe
        const userToRegister = {
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email,
          password: this.user.password,
          role: "user"
        };

        // Appel au service d'inscription
        await AuthService.register(userToRegister);

        // Redirection vers la page de connexion
        this.$router.push('/login');
      } catch (error) {
        this.errorMessage = error.response?.data?.message || "Erreur lors de l'inscription";
        console.error("Erreur d'inscription:", error);
      } finally {
        this.loading = false;
      }
    },
    goToLogin() {
      // Redirection vers la page de connexion
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 2rem 0;
}

.register-form {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
  text-align: left;
}

input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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
}

.btn-register:hover {
  background-color: #0099cc;
}

.btn-register:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
  text-align: left;
}

.login-link {
  margin-top: 1rem;
  text-align: center;
}

.login-link a {
  color: #00c3ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>