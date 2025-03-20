<template>
  <nav class="navbar">
    <div class="logo">
      <a href="#" style="color: white; text-decoration: none;">HotPizza</a>
    </div>

    <ul class="nav-links">
      <li><router-link to="/" class="active">Accueil</router-link></li>
      <li><router-link to="/pizzas">Nos Pizzas</router-link></li>
      <li><router-link to="/ingredients">Nos Ingrédients</router-link></li>
      <li ><router-link to="/panier">Mon Panier</router-link></li>
      <li v-if="isLoggedIn"><router-link to="/compte">Mon Compte</router-link></li>
      <li v-if="isLoggedIn"><router-link to="/admin">Administration</router-link></li>
      <li v-if="isLoggedIn" class="logout-item">
        <button @click="logout" class="logout-button">Se déconnecter</button>
      </li>
      <li v-else>
        <router-link to="/login" class="login-link">Se connecter</router-link>
      </li>
    </ul>
  </nav>
</template>

<script>
import AuthService from '@/services/AuthService'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'NavBar',
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(false)

    // Vérifier si l'utilisateur est connecté au chargement du composant
    onMounted(() => {
      checkLoginStatus()
    })

    // Vérifier l'état de connexion
    const checkLoginStatus = () => {
      isLoggedIn.value = AuthService.isLoggedIn()
    }

    // Fonction de déconnexion
    const logout = () => {
      AuthService.logout()
      isLoggedIn.value = false
      router.push('/login')
    }

    return {
      isLoggedIn,
      logout
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f5f5f5;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #333;
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  list-style: none;
}

.nav-links li {
  margin-left: 1.5rem;
  align-self: center;
}

.nav-links a {
  text-decoration: none;
  color: white;
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-links a:hover, .login-link:hover {
  color: #00c3ff;
}

.nav-links a.active {
  color: #00c3ff;
}

.logout-button {
  background-color: transparent;
  color: white;
  border: 1px solid white;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #00c3ff;
  border-color: #00c3ff;
}

.login-link {
  text-decoration: none;
  color: white;
  font-weight: 500;
  transition: color 0.3s ease;
}

.search-container {
  position: relative;
  margin-left: 1.5rem;
}

.search-container input {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-container button {
  position: absolute;
  right: 0;
  height: 100%;
  background: none;
  border: none;
  color: #333;
  padding: 0 10px;
  cursor: pointer;
}
</style>