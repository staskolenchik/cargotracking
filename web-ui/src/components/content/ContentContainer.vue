<template>
  <md-app-content>
    <slot></slot>
  </md-app-content>
</template>

<script>
  import {mapState} from 'vuex';

  export default {
    name: 'ContentContainer',

    computed: {
      ...mapState({
        authorized: state => state.sidebar.authorized
      })
    }
  };
</script>

<style scoped>

</style>
