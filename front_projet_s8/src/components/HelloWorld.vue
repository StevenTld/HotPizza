<template>
  <nav class="navbar">
    <div class="logo">
      <a href="#" style="color: white; text-decoration: none;">HotPizza</a>
    </div>

    <ul class="nav-links">
      <li><a href="#" class="active">Accueil</a></li>
      <li><a href="#">Nos Pizzas</a></li>
      <li><router-link to="/ingredients">Nos Ingrédients</router-link></li>
      <li><router-link to="/gestionIngredients">Gestion Ingrédients</router-link></li>
      <li><a href="#">Mon Panier</a></li>
      <li><a href="#">Mon Compte</a></li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: 'HelloWorld',
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f5f5f5;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #333;
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  list-style: none;
}

.nav-links li {
  margin-left: 1.5rem;
  align-self: center;
}

.nav-links a {
  text-decoration: none;
  color: white;
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-links a:hover {
  color: #00c3ff;
}

.nav-links a.active {
  color: #00c3ff;
}

.search-container {
  position: relative;
  margin-left: 1.5rem;
}

.search-container input {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-container button {
  position: absolute;
  right: 0;
  height: 100%;
  background: none;
  border: none;
  color: #333;
  padding: 0 10px;
  cursor: pointer;
}


</style>
