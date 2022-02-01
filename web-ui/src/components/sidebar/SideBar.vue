<template>
  <md-app-drawer :md-active.sync="shown" md-swipeable>
    <user-profile-bar/>
    <side-menu/>
  </md-app-drawer>
</template>

<script>
  import UserProfileBar from './UserProfileBar';
  import SideMenu from './SideMenu';

  export default {
    name: 'SideBar',

    computed: {
      shown: {
        get() {
          return this.$store.state.sidebar.shown;
        },
        set(value) {
          this.$store.commit('sidebar/changeVisibility', value);
        }
      }
    },

    components: {SideMenu, UserProfileBar}
  };
</script>

<style scoped>
  .md-drawer {
    width: 300px;
  }
</style>
