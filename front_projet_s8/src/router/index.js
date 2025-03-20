import { createRouter, createWebHistory } from 'vue-router'
import AuthService from '@/services/AuthService'

// Utilisez les chemins réels où vous avez créé ces fichiers
import LoginView from '@/components/LoginView.vue'
import RegisterView from '@/components/RegisterView.vue'
import HomeView from '@/components/HomeView.vue'
import IngredientListView from "@/components/IngredientListView.vue";
import Gestion_Ingredient from "@/components/Gestion_Ingredient.vue"
import PizzaListView from "@/components/PizzaListView.vue";
import AdminView from "@/components/AdminView.vue";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView,
        meta: {
            requiresAuth: false
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: {
            requiresAuth: false
        }
    },
    {
      path: '/gestionIngredient',
      name: 'GestionIngredient',
      component: Gestion_Ingredient,
        meta: {
          requiresAuth: true
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView,
        meta: {
            requiresAuth: false
        }
    },
    {
        path: '/ingredients',
        name: 'Ingredients',
        component: IngredientListView,
        meta: {
            requiresAuth: false
        }
    },
    {
        path: '/pizzas',
        name: 'Pizzas',
        component: PizzaListView,
        meta: {
            requiresAuth: false
        }
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminView,
        meta: {
            requiresAuth: true
        }
    },
    // Ajoutez ici toutes vos autres routes
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// Garde de navigation pour protéger les routes
router.beforeEach((to, from, next) => {
    // Vérifie si la route nécessite une authentification
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

    // Vérifie si l'utilisateur est connecté
    const isLoggedIn = AuthService.isLoggedIn()

    // Si la route nécessite une authentification et que l'utilisateur n'est pas connecté, rediriger vers la page de connexion
    if (requiresAuth && !isLoggedIn) {
        next('/login')
    } else {
        next()
    }
})

export default router