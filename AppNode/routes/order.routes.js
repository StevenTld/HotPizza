// routes/order.routes.js - Routes pour l'API Order
const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middlewares/auth.middleware');
const orderService = require('../services/order.service');

// Middleware de vérification du token pour toutes les routes
router.use(verifyToken);

// Récupérer toutes les commandes de l'utilisateur connecté
router.get('/', async (req, res, next) => {
    try {
        const orders = await orderService.getUserOrders(req.user.userId);
        res.json(orders);
    } catch (error) {
        next(error);
    }
});

// Récupérer une commande par ID
router.get('/:id', async (req, res, next) => {
    try {
        const order = await orderService.getOrderById(req.params.id, req.user.userId);
        res.json(order);
    } catch (error) {
        next(error);
    }
});

// Créer une nouvelle commande
router.post('/', async (req, res, next) => {
    try {
        const createdOrder = await orderService.createOrder(req.body, req.user.userId);
        res.status(201).json(createdOrder);
    } catch (error) {
        next(error);
    }
});

// Dans votre route
router.put('/:id/status', async (req, res, next) => {
    try {
        const { status } = req.query;
        console.log('Statut reçu:', status); // Log du statut

        if (!status) {
            return res.status(400).json({ message: 'Le statut est requis' });
        }
       
        const updatedOrder = await orderService.updateOrderStatus(req.params.id, status, req.user.userId);
        res.json(updatedOrder);
    } catch (error) {
        console.error('Erreur détaillée:', error.response ? error.response.data : error.message);
        next(error);
    }
});

// Marquer une commande comme payée
router.put('/:id/payment', async (req, res, next) => {
    try {
        const { transactionId } = req.query;
        if (!transactionId) {
            return res.status(400).json({ message: 'L\'ID de transaction est requis' });
        }
        
        const updatedOrder = await orderService.markOrderAsPaid(req.params.id, transactionId, req.user.userId);
        res.json(updatedOrder);
    } catch (error) {
        next(error);
    }
});

// Supprimer une commande
router.delete('/:id', async (req, res, next) => {
    try {
        await orderService.deleteOrder(req.params.id, req.user.userId);
        res.status(204).end();
    } catch (error) {
        next(error);
    }
});

module.exports = router;