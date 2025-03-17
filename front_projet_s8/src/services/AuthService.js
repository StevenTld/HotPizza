import axios from 'axios'

const API_URL = 'http://localhost:3000/api/auth'

class AuthService {
    // Login - Authentification
    async login(credentials) {
        const response = await axios.post(`${API_URL}/login`, credentials)
        if (response.data.token) {
            localStorage.setItem('token', response.data.token)
        }
        return response.data
    }

    // Inscription
    async register(user) {
        const response = await axios.post(`${API_URL}/register`, user)
        return response.data
    }

    // Déconnexion
    logout() {
        localStorage.removeItem('token')
    }

    // Vérifier si l'utilisateur est connecté
    isLoggedIn() {
        return !!localStorage.getItem('token')
    }

    // Récupérer le token
    getToken() {
        return localStorage.getItem('token')
    }

    // Configurer le header d'autorisation pour les requêtes
    setupAxiosInterceptors() {
        axios.interceptors.request.use(
            config => {
                const token = this.getToken()
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`
                }
                return config
            },
            error => {
                return Promise.reject(error)
            }
        )
    }
}

export default new AuthService()