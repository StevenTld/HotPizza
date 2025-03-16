// auth-server.js
const express = require('express');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;
const JWT_SECRET = 'votre_secret_jwt'; 

app.use(bodyParser.json());
app.use(cors());

// Route pour la connexion - communique avec le backend Spring
app.post('/auth/login', async (req, res) => {
    try {
        // Appel au backend Spring pour vérifier les identifiants
        const response = await axios.post('http://localhost:8080/api/auth/login', {
            email: req.body.email,
            password: req.body.password
        });
        
        // Si la connexion est réussie côté Spring, on génère un token
        const userData = response.data;
        
        // Générer le JWT avec les données utilisateur
        const token = jwt.sign(
            { 
                userId: userData.userId,
                email: userData.email
            }, 
            JWT_SECRET, 
            { expiresIn: '24h' }
        );
        
        // Renvoyer le token avec les données utilisateur
        res.json({
            token,
            user: userData
        });
    } catch (error) {
        // En cas d'erreur, on la renvoie
        res.status(error.response?.status || 500).json({
            message: error.response?.data?.message || 'Erreur lors de la connexion'
        });
    }
});

// Route pour l'inscription - communique avec le backend Spring
app.post('/auth/register', async (req, res) => {
    try {
        // Appel au backend Spring pour créer l'utilisateur
        const response = await axios.post('http://localhost:8080/api/auth/register', {
            email: req.body.email,
            password: req.body.password
        });
        
        // Si l'inscription est réussie, on renvoie les données utilisateur
        res.status(201).json(response.data);
    } catch (error) {
        res.status(error.response?.status || 500).json({
            message: error.response?.data?.message || 'Erreur lors de l\'inscription'
        });
    }
});

// Middleware pour vérifier les tokens JWT
const verifyToken = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    
    if (!token) {
        return res.status(403).json({ message: 'Aucun token fourni' });
    }
    
    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        return res.status(401).json({ message: 'Token invalide ou expiré' });
    }
};

// Route protégée pour tester les tokens
app.get('/auth/me', verifyToken, (req, res) => {
    res.json(req.user);
});

app.listen(PORT, () => {
    console.log(`Serveur d'authentification démarré sur le port ${PORT}`);
});