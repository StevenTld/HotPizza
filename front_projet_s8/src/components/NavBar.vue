<template>
  <nav class="navbar">
    <div class="logo">
      <a href="/" style="color: white; text-decoration: none;">HotPizza</a>
    </div>

    <ul class="nav-links">
      <li><router-link to="/" class="active">Accueil</router-link></li>
      <li><router-link to="/pizzas">Nos Pizzas</router-link></li>
      <li><router-link to="/ingredients">Nos Ingrédients</router-link></li>
      <li>
        <router-link to="/panier" class="cart-icon-link">
          <i class="fas fa-shopping-cart"></i>
          <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
        </router-link>
      </li>
      <li v-if="isLoggedIn"><router-link :to="isAdmin ? '/compte/admin' : '/compte'">Mon Compte</router-link></li>
      <li v-if="isAdmin"><router-link to="/admin">Administration</router-link></li>
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
import CartService from '@/services/CartService'
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'NavBar',
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(false)
    const isAdmin = ref(false)
    const cartItemCount = ref(0)

    // Vérifier si l'utilisateur est connecté au chargement du composant
    onMounted(async () => {
      checkLoginStatus();
      await checkAdminStatus();
      if (isLoggedIn.value) {
        await updateCartCount();
      }
    })

    // Observer les changements d'état de connexion
    watch(isLoggedIn, async (newValue) => {
      if (newValue) {
        await updateCartCount();
      } else {
        cartItemCount.value = 0;
      }
    });

    // Vérifier l'état de connexion
    const checkLoginStatus = () => {
      isLoggedIn.value = AuthService.isLoggedIn()
    }

    const checkAdminStatus = async () => {
      try {
        // Utiliser la méthode asynchrone qui fait une requête API
        await AuthService.fetchUserRole(true); // true pour forcer le rafraîchissement
        isAdmin.value = AuthService.isAdmin();
      } catch (error) {
        console.error("Erreur lors de la vérification du statut admin:", error);
        isAdmin.value = false;
      }
    }

    // Fonction pour mettre à jour le compteur du panier
    const updateCartCount = async () => {
      try {
        const cart = await CartService.getUserCart();
        if (cart && cart.items) {
          // Calcule le nombre total d'articles dans le panier
          cartItemCount.value = cart.items.reduce((total, item) => total + (item.quantity || 1), 0);
        } else {
          cartItemCount.value = 0;
        }
      } catch (error) {
        console.error("Erreur lors de la récupération du panier:", error);
        cartItemCount.value = 0;
      }
    }

    // Fonction de déconnexion
    const logout = () => {
      AuthService.logout()
      isLoggedIn.value = false
      isAdmin.value = false
      cartItemCount.value = 0
      router.push('/')
    }

    // Exposer les méthodes pour permettre une mise à jour depuis d'autres composants
    if (window) {
      window.updateNavbarCart = updateCartCount;
    }

    return {
      isLoggedIn,
      logout,
      isAdmin,
      cartItemCount,
      updateCartCount
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

/* Styles pour l'icône du panier */
.cart-icon-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  padding: 5px;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #e74c3c;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

/* Animation du badge lorsqu'il change */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.cart-badge {
  animation: pulse 0.3s;
}
</style>