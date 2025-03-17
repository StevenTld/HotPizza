import { createRouter, createWebHistory } from 'vue-router'
import Gestion_Ingredient from "@/components/Gestion_Ingredient.vue";
import IngredientListView from "@/components/IngredientListView.vue";

const routes = [
    {
        path: '/ingredients',
        name: 'Ingredients',
        component: IngredientListView
    },
    {
        path: '/gestionIngredients',
        name: 'GestionIngredients',
        component: Gestion_Ingredient
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router