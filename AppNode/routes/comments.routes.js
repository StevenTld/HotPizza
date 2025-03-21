// routes/comments.routes.js
const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middlewares/auth.middleware');
const commentsService = require('../services/comments.service');

// Récupérer tous les commentaires d'une pizza (pas besoin d'authentification)
router.get('/pizza/:pizzaId', async (req, res, next) => {
    try {
        const comments = await commentsService.getCommentsByPizzaId(req.params.pizzaId);
        res.json(comments);
    } catch (error) {
        next(error);
    }
});

// Récupérer la note moyenne d'une pizza (pas besoin d'authentification)
router.get('/pizza/:pizzaId/rating', async (req, res, next) => {
    try {
        const rating = await commentsService.getAverageRatingForPizza(req.params.pizzaId);
        res.json(rating);
    } catch (error) {
        next(error);
    }
});

// Récupérer les commentaires de l'utilisateur connecté (nécessite authentification)
router.get('/user', verifyToken, async (req, res, next) => {
    try {
        const comments = await commentsService.getUserComments(req.user.userId);
        res.json(comments);
    } catch (error) {
        next(error);
    }
});

// Récupérer un commentaire par ID (pas besoin d'authentification)
router.get('/:id', async (req, res, next) => {
    try {
        const comment = await commentsService.getCommentById(req.params.id);
        res.json(comment);
    } catch (error) {
        next(error);
    }
});

// Ajouter un nouveau commentaire (nécessite authentification)
router.post('/', verifyToken, async (req, res, next) => {
    try {
        const commentData = {
            ...req.body,
            userId: req.user.userId,
            userName: `${req.user.firstName} ${req.user.lastName}`.trim()
        };

        const createdComment = await commentsService.createComment(commentData);
        res.status(201).json(createdComment);
    } catch (error) {
        next(error);
    }
});

// Mettre à jour un commentaire (nécessite authentification)
router.put('/:id', verifyToken, async (req, res, next) => {
    try {
        const updatedComment = await commentsService.updateComment(
            req.params.id,
            req.body,
            req.user.userId,
            req.user.role === 'ADMIN'
        );
        res.json(updatedComment);
    } catch (error) {
        next(error);
    }
});

// Supprimer un commentaire (nécessite authentification)
router.delete('/:id', verifyToken, async (req, res, next) => {
    try {
        const result = await commentsService.deleteComment(
            req.params.id,
            req.user.userId,
            req.user.role === 'ADMIN'
        );
        res.json(result);
    } catch (error) {
        next(error);
    }
});

module.exports = router;