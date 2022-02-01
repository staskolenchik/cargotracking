<template>
  <md-app-toolbar class="md-primary">
    <md-button class="md-icon-button" @click="showBar">
      <md-icon>menu</md-icon>
    </md-button>
    <span class="md-title">{{ normalizedTitle }}</span>
  </md-app-toolbar>
</template>

<script>
  export default {
    name: 'ToolBar',

    methods: {
      showBar() {
        this.$store.commit('sidebar/changeVisibility', true);
      }
    },

    props: {
      title: String
    },

    computed: {
      normalizedTitle: function() {
        const splitted = this.title.replace('-', ' ').split('/');
        return splitted[1].charAt(0).toUpperCase() + splitted[1].slice(1).toLowerCase();
      }
    }
  };
</script>

<style scoped>

</style>
