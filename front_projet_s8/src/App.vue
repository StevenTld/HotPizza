<template>
  <div id="app">
    <NavBar v-if="showNav"/>
    <router-view/>
  </div>
</template>

<script>
import NavBar from './components/NavBar.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'

export default {
  name: 'App',
  components: {
    NavBar
  },
  setup() {
    const route = useRoute()

    // Nous cachons la navigation pour les pages d'authentification
    const showNav = computed(() => {
      return !['Login', 'Register'].includes(route.name)
    })

    return {
      showNav
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

/* Réinitialisation CSS générale */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
}
</style>