<template>
  <HelloWorld></HelloWorld>
  <router-view class="main-content"></router-view>
</template>

<script>

import HelloWorld from "@/components/HelloWorld.vue";

export default {
  name: 'App',
  components: {
    HelloWorld
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
