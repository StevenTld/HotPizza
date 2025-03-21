const express = require('express');
const router = express.Router();
const commentService = require('../services/comment.service');
// routes/order.routes.js - Routes pour l'API Order

const { verifyToken } = require('../middlewares/auth.middleware');

// Middleware de vérification du token pour toutes les routes
router.use(verifyToken);
// Ajouter un commentaire
router.post('/',  async (req, res, next) => {
    try {
        const newComment = await commentService.addComment(req.body, req.user.userId);
        res.status(201).json(newComment);
    } catch (error) {
        next(error);
    }
});

// Récupérer les commentaires d'une pizza
router.get('/pizza/:pizzaId',  async (req, res, next) => {
    try {
        const comments = await commentService.getCommentsByPizzaId(req.params.pizzaId, req.user.userId);
        res.json(comments);
    } catch (error) {
        next(error);
    }
});

// Récupérer la note moyenne d'une pizza
router.get('/pizza/:pizzaId/rating', async (req, res, next) => {
    try {
        const averageRating = await commentService.getPizzaAverageRating(req.params.pizzaId, req.user.userId);
        res.json(averageRating);
    } catch (error) {
        next(error);
    }
});

// Mettre à jour un commentaire
router.put('/:commentId',  async (req, res, next) => {
    try {
        const updatedComment = await commentService.updateComment(req.params.commentId, req.body, req.user.userId);
        if (!updatedComment) {
            return res.status(404).json({ message: 'Commentaire non trouvé' });
        }
        res.json(updatedComment);
    } catch (error) {
        next(error);
    }
});

// Supprimer un commentaire
router.delete('/:commentId',async (req, res, next) => {
    try {
        const result = await commentService.deleteComment(req.params.commentId, req.user.userId);
        if (!result) {
            return res.status(404).json({ message: 'Commentaire non trouvé' });
        }
        res.json({ message: 'Commentaire supprimé avec succès' });
    } catch (error) {
        next(error);
    }
});

module.exports = router;