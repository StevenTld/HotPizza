<template>
  <div class="login-container">
    <div class="login-form">
      <div class="back-button" @click="goBack">
        <span class="arrow">&larr;</span> Retour
      </div>
      <h2>Connexion</h2>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="email">Email</label>
          <input
              type="email"
              id="email"
              v-model="credentials.email"
              placeholder="Votre email"
              required
          />
        </div>
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              type="password"
              id="password"
              v-model="credentials.password"
              placeholder="Votre mot de passe"
              required
          />
        </div>
        <div class="error-message" v-if="errorMessage">{{ errorMessage }}</div>
        <button type="submit" class="btn-login" :disabled="loading">
          {{ loading ? 'Connexion en cours...' : 'Se connecter' }}
        </button>
      </form>
      <div class="register-link">
        <p>Pas encore de compte ? <a href="#" @click.prevent="goToRegister">S'inscrire</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'LoginView',
  data() {
    return {
      credentials: {
        email: '',
        password: ''
      },
      errorMessage: '',
      loading: false
    }
  },
  methods: {
    async login() {
      try {
        this.loading = true;
        this.errorMessage = '';

        // Appel à votre API d'authentification existante
        const response = await axios.post('http://localhost:3000/api/auth/login', this.credentials);

        // Stockage du token dans le localStorage
        localStorage.setItem('token', response.data.token);

        // Rediriger vers la page d'accueil après connexion réussie
        this.$router.push('/');

      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Erreur lors de la connexion';
        console.error('Erreur de connexion:', error);
      } finally {
        this.loading = false;
      }
    },
    goToRegister() {
      // Redirection vers la page d'inscription
      this.$router.push('/register');
    },
    goBack() {
      // Retour à la page précédente
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  position: relative;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  color: #666;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  transition: color 0.3s;
}

.back-button:hover {
  color: #00c3ff;
}

.arrow {
  margin-right: 5px;
  font-size: 1.2rem;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
  margin-top: 1rem;
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

.btn-login {
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

.btn-login:hover {
  background-color: #0099cc;
}

.btn-login:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
  text-align: left;
}

.register-link {
  margin-top: 1rem;
  text-align: center;
}

.register-link a {
  color: #00c3ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>