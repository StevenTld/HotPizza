import { createRouter, createWebHistory } from 'vue-router'
import AuthService from '@/services/AuthService'

// Utilisez les chemins réels où vous avez créé ces fichiers
import LoginView from '@/components/LoginView.vue'
import RegisterView from '@/components/RegisterView.vue'
import HomeView from '@/components/HomeView.vue'
import IngredientListView from "@/components/IngredientListView.vue"
import Gestion_Ingredient from "@/components/Gestion_Ingredient.vue"
import PizzaListView from "@/components/PizzaListView.vue"
import AdminView from "@/components/AdminView.vue"
import CartView from '@/components/CartView.vue'
import CheckoutView from '@/components/CheckoutView.vue'
import OrderHistoryView from '@/components/OrderHistoryView.vue'

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
            requiresAuth: true,
            requiresAdmin:true

        }
    },
    {
        path: '/panier',
        name: 'Cart',
        component: CartView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/checkout',
        name: 'Checkout',
        component: CheckoutView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/compte/commandes',
        name: 'OrderHistory',
        component: OrderHistoryView,
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
router.beforeEach(async (to, from, next) => {
    // Vérifie si la route nécessite une authentification
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

    // Vérifie si la route nécessite des droits d'administrateur
    const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)

    // Vérifie si l'utilisateur est connecté
    const isLoggedIn = AuthService.isLoggedIn()

    // Si l'utilisateur n'est pas connecté et que la route nécessite une authentification
    if (requiresAuth && !isLoggedIn) {
        next('/login')
        return
    }

    // Si la route nécessite des droits d'administrateur
    if (requiresAdmin) {
        try {
            // Récupérer le rôle de l'utilisateur
            const userRole = await AuthService.fetchUserRole()

            // Si l'utilisateur n'est pas admin, rediriger vers la page d'accueil
            if (userRole !== 'admin') {
                next('/')
                return
            }
        } catch (error) {
            console.error("Erreur lors de la vérification du rôle admin:", error)
            next('/')
            return
        }
    }

    // Dans tous les autres cas, permettre l'accès à la route
    next()
})

export default router