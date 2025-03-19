// routes/ingredients.routes.js - Routes pour l'API Ingredients
const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middlewares/auth.middleware');
const pizzaService = require('../services/pizza.service');

// Middleware de vérification du token pour toutes les routes

// Récupérer tous les ingrédients
router.get('/', async (req, res, next) => {
    try {
        const pizzas = await pizzaService.getAllPizzas();
        res.json(pizzas);
    } catch (error) {
        next(error);
    }
});

module.exports = router;
