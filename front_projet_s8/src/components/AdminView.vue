// AdminView.vue
<template>
  <div class="admin-container">
    <Gestion_Ingredient />
    <Gestion_Pizza />
  </div>
</template>

<script>
import Gestion_Ingredient from "@/components/Gestion_Ingredient.vue";
import Gestion_Pizza from "@/components/Gestion_Pizza.vue";

export default {
  name: 'AdminView',
  components: {
    Gestion_Ingredient,
    Gestion_Pizza
  }
}
</script>