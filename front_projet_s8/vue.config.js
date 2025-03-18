const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8079 // Choisissez un port disponible (8081, 8082, etc.)
  }
})
