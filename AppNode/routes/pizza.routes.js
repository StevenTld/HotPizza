// routes/pizza.routes.js
const express = require('express');
const router = express.Router();
const pizzaService = require('../services/pizza.service');

// Pas de middleware de vérification de token
router.get('/', async (req, res, next) => {
    try {
        const pizzas = await pizzaService.getAllPizzas();
        res.json(pizzas);
    } catch (error) {
        next(error);
    }
});

module.exports = router;