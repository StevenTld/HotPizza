// routes/cart.routes.js - Routes pour l'API Cart
const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middlewares/auth.middleware');
const cartService = require('../services/cart.service');

// Middleware de vérification du token pour toutes les routes
router.use(verifyToken);

// Middleware de diagnostic
router.use((req, res, next) => {
    console.log("DIAGNOSTIC immédiat après verifyToken - req.user:", req.user);
    next();
});
router.get('/', async (req, res, next) => {
    console.log("Début de la route GET /");
    try {
        // Vérification de sécurité
        if (!req.user) {
            console.log("req.user est undefined dans la route!");
            return res.status(401).json({ message: 'Utilisateur non authentifié' });
        }
        
        console.log("req.user dans la route:", req.user);
        const cart = await cartService.getUserCart(req.user.userId);
        res.json(cart);
    } catch (error) {
        console.log("Erreur dans la route:", error);
        next(error);
    }
});

// Ajouter une pizza au panier
router.post('/pizzas', async (req, res, next) => {
    try {
        const { pizzaId, quantity } = req.query;
        if (!pizzaId) {
            return res.status(400).json({ message: 'L\'ID de la pizza est requis' });
        }
        
        const updatedCart = await cartService.addPizzaToCart(
            pizzaId,
            quantity || 1,
            req.user.userId
        );
        res.json(updatedCart);
    } catch (error) {
        next(error);
    }
});

// Ajouter un ingrédient supplémentaire à une pizza
router.post('/pizzas/:pizzaId/extras', async (req, res, next) => {
    try {
        const { pizzaId } = req.params;
        const { ingredientId, quantity } = req.query;
        
        if (!ingredientId) {
            return res.status(400).json({ message: 'L\'ID de l\'ingrédient est requis' });
        }
        
        const updatedCart = await cartService.addExtraIngredientToCart(
            pizzaId,
            ingredientId,
            quantity || 1,
            req.user.userId
        );
        res.json(updatedCart);
    } catch (error) {
        next(error);
    }
});

// Supprimer une pizza du panier
router.delete('/pizzas/:pizzaId', async (req, res, next) => {
    try {
        const { pizzaId } = req.params;
        
        const updatedCart = await cartService.removePizzaFromCart(
            pizzaId,
            req.user.userId
        );
        res.json(updatedCart);
    } catch (error) {
        next(error);
    }
});

// Supprimer un ingrédient supplémentaire d'une pizza
router.delete('/pizzas/:pizzaId/extras/:ingredientId', async (req, res, next) => {
    try {
        const { pizzaId, ingredientId } = req.params;
        
        const updatedCart = await cartService.removeExtraIngredientFromCart(
            pizzaId,
            ingredientId,
            req.user.userId
        );
        res.json(updatedCart);
    } catch (error) {
        next(error);
    }
});

// Vider le panier
router.delete('/clear', async (req, res, next) => {
    try {
        const emptyCart = await cartService.clearCart(
            req.user.userId
        );
        res.json(emptyCart);
    } catch (error) {
        next(error);
    }
});

module.exports = router;