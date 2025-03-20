// routes/ingredients.routes.js - Routes pour l'API Ingredients
const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middlewares/auth.middleware');
const ingredientsService = require('../services/ingredients.service');

// Middleware de vérification du token pour toutes les routes
//router.use(verifyToken);

// Récupérer tous les ingrédients
router.get('/', async (req, res, next) => {
    try {
        const ingredients = await ingredientsService.getAllIngredients();
        res.json(ingredients);
    } catch (error) {
        next(error);
    }
});

// Récupérer un ingrédient par ID
router.get('/:id', async (req, res, next) => {
    try {
        const ingredient = await ingredientsService.getIngredientById(req.params.id);
        res.json(ingredient);
    } catch (error) {
        next(error);
    }
});

// Créer un nouvel ingrédient
router.post('/', async (req, res, next) => {
    try {
        const createdIngredient = await ingredientsService.createIngredient(req.body);
        res.status(201).json(createdIngredient);
    } catch (error) {
        next(error);
    }
});

// Supprimer un ingrédient
router.delete('/:id', async (req, res, next) => {
    try {
        const result = await ingredientsService.deleteIngredient(req.params.id);
        res.json(result);
    } catch (error) {
        next(error);
    }
});

module.exports = router;