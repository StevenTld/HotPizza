// auth.middleware.js - Middleware d'authentification
const jwt = require('jsonwebtoken');

// Middleware de vérification des tokens JWT
const verifyToken = (req, res, next) => {
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ message: 'Authentification requise' });
    }
    
    const token = authHeader.split(' ')[1];
    
    try {
        // Vérifier le token avec la clé secrète
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({ message: 'Session expirée' });
        }
        return res.status(401).json({ message: 'Token invalide' });
    }
};

module.exports = { verifyToken };