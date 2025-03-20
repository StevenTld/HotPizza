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

// Récupérer une pizza par son ID
router.get('/:id', async (req, res, next) => {
    try {
        const pizza = await pizzaService.getPizzaById(req.params.id);
        if (!pizza) {
            return res.status(404).json({ message: 'Pizza non trouvée' });
        }
        res.json(pizza);
    } catch (error) {
        next(error);
    }
});

// Créer une nouvelle pizza
router.post('/', async (req, res, next) => {
    try {
        const newPizza = await pizzaService.createPizza(req.body);
        res.status(201).json(newPizza);
    } catch (error) {
        next(error);
    }
});

// Mettre à jour une pizza existante
router.put('/:id', async (req, res, next) => {
    try {
        const updatedPizza = await pizzaService.updatePizza(req.params.id, req.body);
        if (!updatedPizza) {
            return res.status(404).json({ message: 'Pizza non trouvée' });
        }
        res.json(updatedPizza);
    } catch (error) {
        next(error);
    }
});

// Supprimer une pizza
router.delete('/:id', async (req, res, next) => {
    try {
        const result = await pizzaService.deletePizza(req.params.id);
        if (!result) {
            return res.status(404).json({ message: 'Pizza non trouvée' });
        }
        res.json({ message: 'Pizza supprimée avec succès' });
    } catch (error) {
        next(error);
    }
});

module.exports = router;