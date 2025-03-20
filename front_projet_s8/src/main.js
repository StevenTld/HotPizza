import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import AuthService from './services/AuthService'

// Configuration des intercepteurs axios pour les requêtes authentifiées
AuthService.setupAxiosInterceptors()

// Création de l'application Vue avec le routeur
createApp(App)
    .use(router)
    .mount('#app')