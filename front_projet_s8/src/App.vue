<template>
  <HelloWorld/>
  <IngredientCard/>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import IngredientListView from "@/components/IngredientListView.vue";

export default {
  name: 'App',
  components: {
    HelloWorld,
    IngredientCard: IngredientListView
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
</style>
