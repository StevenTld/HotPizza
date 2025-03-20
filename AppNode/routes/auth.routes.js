// routes/auth.routes.js - Routes pour l'API d'authentification
const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const { verifyToken } = require('../middlewares/auth.middleware');
const authService = require('../services/auth.service');

// Route de connexion
router.post('/login', async (req, res, next) => {
    try {
        // Vérifier les identifiants avec le service
        const userData = await authService.verifyCredentials(req.body);

        // Générer un token JWT
        const token = jwt.sign(
            {
                userId: userData.userId,
                email: userData.email,
                role: userData.role || 'USER'
            },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRY || '24h' }
        );

        // Renvoyer le token et les données utilisateur
        res.json({
            token,
            user: userData
        });
    } catch (error) {
        next(error);
    }
});

// Route d'inscription
router.post('/register', async (req, res, next) => {
    try {
        const createdUser = await authService.registerUser(req.body);
        res.status(201).json(createdUser);
    } catch (error) {
        next(error);
    }
});

// Route pour vérifier le token et obtenir les infos utilisateur
router.get('/me', verifyToken, (req, res) => {
    res.json(req.user);
});

module.exports = router;