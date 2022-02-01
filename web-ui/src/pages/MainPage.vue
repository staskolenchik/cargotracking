<template>
  <div class="page-container">
    <md-app
      md-waterfall
      md-mode="fixed"
    >
      <side-bar slot="md-app-drawer"/>
      <tool-bar
        :title="$router.currentRoute.path"
        slot="md-app-toolbar"
      />
      <content-container slot="md-app-content">
        <transition name="fade">
          <div
            class="fade-notification"
            v-if="messageTaken"
            @click="messageTaken = false"
          >
            {{ messageText }}
          </div>
        </transition>

        <router-view></router-view>
      </content-container>
    </md-app>
  </div>
</template>

<script>
  import {mapActions, mapState} from 'vuex';

  import ToolBar from '../components/toolbar/ToolBar';
  import SideBar from '../components/sidebar/SideBar';
  import ContentContainer from '../components/content/ContentContainer';

  export default {
    name: 'MainPage',

    computed: {
      ...mapState({
        messageText: state => state.websocket.messageText
      }),
      messageTaken: {
        get() {
          return this.$store.state.websocket.messageTaken;
        },
        set(value) {
          this.$store.commit('websocket/changeMessageTaken', value);
        }
      }
    },

    methods: {
      ...mapActions('sidebar', [
        'hideBar'
      ])
    },

    components: {ContentContainer, ToolBar, SideBar}
  };
</script>

<style scoped>
  .page-container
  .md-app {
    height: 100vh;
  }

  .fade-notification {
    max-width: 300px;
    z-index: 10;
    padding: 20px;
    right: 5px;
    position: absolute;
    top: 70px;
    color: #155724;
    background-color: #d4edda;
    border: 1px solid;
    border-color: #c3e6cb;
    cursor: pointer;
  }

  .fade-enter-active, .fade-leave-active {
    transition: all .5s;
  }
  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>

