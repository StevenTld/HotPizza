// server.js - API Gateway simplifiée
require('dotenv').config();
const jwt = require('jsonwebtoken');
const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/auth.routes');
const ingredientsRoutes = require('./routes/ingredients.routes');
const pizzaRoutes = require('./routes/pizza.routes');
const orderRoutes = require('./routes/order.routes');
const cartRoutes = require('./routes/cart.routes');
const commentsRoutes = require('./routes/comments.routes');

const app = express();
const PORT = process.env.PORT || 3000;

// Configurer CORS pour permettre les requêtes depuis Vue.js
app.use(cors({
  origin: 'http://localhost:8079', // URL de votre application Vue.js
  credentials: true                // Permet l'envoi de cookies si nécessaire
}));

// Middlewares de base
app.use(express.json());

// Routes API
app.use('/api/auth', authRoutes);
app.use('/api/ingredients', ingredientsRoutes);
app.use('/api/pizza', pizzaRoutes);
app.use('/api/order', orderRoutes);
app.use('/api/cart', cartRoutes);
app.use('/api/comments', commentsRoutes);
// Plus besoin de servir des fichiers statiques ni de route par défaut

// Gestion des erreurs globale
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(err.statusCode || 500).json({
    message: err.message || 'Une erreur est survenue'
  });
});

// Dans server.js
app.get('/api/debug-token', (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.json({ error: 'No token' });
  }
  
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    return res.json({ 
      success: true, 
      decoded,
      jwt_secret_defined: !!process.env.JWT_SECRET,
      jwt_secret_length: process.env.JWT_SECRET?.length
    });
  } catch (err) {
    return res.json({ 
      error: err.message, 
      type: err.name,
      jwt_secret_defined: !!process.env.JWT_SECRET,
      jwt_secret_length: process.env.JWT_SECRET?.length
    });
  }
});
// Démarrage du serveur
app.listen(PORT, () => {
  console.log(`API Gateway démarrée sur http://localhost:${PORT}`);
});