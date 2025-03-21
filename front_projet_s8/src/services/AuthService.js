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

    async updateUser(updateData) {
        try {
            const response = await axios.put(`${API_URL}/me`, updateData, {
                headers: {
                    Authorization: `Bearer ${this.getToken()}`
                }
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la mise à jour des informations utilisateur:', error);
            throw error;
        }
    }

    // Inscription
    async register(user) {
        console.log("Données d'inscription envoyées:", user);
        try {
            const response = await axios.post(`${API_URL}/register`, user);
            console.log("Réponse d'inscription:", response.data);
            return response.data;
        } catch (error) {
            console.error("Erreur d'inscription détaillée:", {
                status: error.response?.status,
                statusText: error.response?.statusText,
                data: error.response?.data,
                headers: error.response?.headers
            });
            throw error;
        }
    }

    // Déconnexion
    logout() {
        localStorage.removeItem('token')
    }

    // Vérifier si l'utilisateur est connecté
    isLoggedIn() {
        return !!localStorage.getItem('token')
    }

    // Méthode pour vérifier si l'utilisateur est admin (version synchrone)
    isAdmin() {
        return this.userRole === 'admin';
    }

    // Méthode pour récupérer et mettre en cache le rôle utilisateur
    async fetchUserRole() {
        try {
            console.log('Fetching user role...');
            const response = await axios.get(`${API_URL}/me`, {
                headers: {
                    Authorization: `Bearer ${this.getToken()}`
                }
            });

            console.log('Response data:', response.data);
            this.userRole = response.data.role;
            console.log('User role set to:', this.userRole);
            return this.userRole;
        } catch (error) {
            console.error('Erreur lors de la récupération du rôle:', error);
            return null;
        }
    }
    // Récupérer les informations de l'utilisateur connecté
    async getCurrentUser() {
        try {
            const response = await axios.get(`${API_URL}/me`, {
                headers: {
                    Authorization: `Bearer ${this.getToken()}`
                }
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des informations utilisateur:', error);
            return null;
        }
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