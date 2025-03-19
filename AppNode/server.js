// server.js - API Gateway simplifiée
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/auth.routes');
const ingredientsRoutes = require('./routes/ingredients.routes');
const pizzaRoutes = require('./routes/pizza.routes');

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

// Plus besoin de servir des fichiers statiques ni de route par défaut

// Gestion des erreurs globale
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(err.statusCode || 500).json({
    message: err.message || 'Une erreur est survenue'
  });
});

// Démarrage du serveur
app.listen(PORT, () => {
  console.log(`API Gateway démarrée sur http://localhost:${PORT}`);
});