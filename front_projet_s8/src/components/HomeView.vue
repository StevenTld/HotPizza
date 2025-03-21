<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1>Bienvenue chez HotPizza</h1>
      <p>Découvrez nos délicieuses pizzas artisanales</p>
      <div class="cta-buttons">
        <router-link to="/pizzas" class="btn-primary">Voir nos pizzas</router-link>
        <router-link to="/panier" class="btn-secondary">Commander maintenant</router-link>
      </div>
    </div>
  </div>
</template>



<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-section {
  text-align: center;
  padding: 60px 20px;
  background-color: #f8f8f8;
  border-radius: 8px;
  margin-bottom: 40px;
}

h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 15px;
}

.welcome-section p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 30px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn-primary {
  background-color: #00c3ff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #0099cc;
}

.btn-secondary {
  background-color: white;
  color: #00c3ff;
  border: 2px solid #00c3ff;
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background-color: #f0f9ff;
}

.featured-section {
  padding: 20px 0;
}

.featured-section h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 15px;
  text-align: center;
}

.featured-section p {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}
</style>