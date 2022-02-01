<template>
  <md-card class="user-profile-card">
    <md-button class="md-icon-button" @click="hideBar">
      <md-icon>menu</md-icon>
    </md-button>

    <md-card-header>
      <md-card-header-text v-if="authorized">
        <div class="md-title">Welcome,</div>
        <div class="md-subhead">{{username}}</div>
      </md-card-header-text>

      <md-card-header-text v-if="!authorized">
        <div class="md-title">You not authorized!</div>
      </md-card-header-text>
    </md-card-header>

    <md-card-actions>
      <md-button
        v-if="authorized && !userRoles.includes('SYS_ADMIN') && !userRoles.includes('ADMIN')"
        class="md-primary"
        @click="hideBar"
      >
        <md-icon>lock</md-icon>
        <router-link to="/profile">
          Profile
        </router-link>
      </md-button>

      <md-button
        class="md-primary"
        v-if="authorized"
        @click="logout"
      >
        <md-icon>exit_to_app</md-icon>
        <span>Logout</span>
      </md-button>

      <md-button
        class="md-primary"
        v-if="!authorized"
        to="/login"
      >
        <md-icon>exit_to_app</md-icon>
        <span>Login</span>
      </md-button>
    </md-card-actions>
  </md-card>
</template>

<script>
  import {mapState} from 'vuex';

  export default {
    name: 'UserProfileBar',

    data: () => ({
      userRoles: [],
      intervalFunction: null
    }),

    methods: {
      hideBar() {
        this.$store.commit('sidebar/changeVisibility', false);
      },

      refreshTokens() {
        const refreshTokenPayload = JSON.parse(atob(localStorage.refreshToken.split('.')[1]));

        this.$http.post('/api/refresh', {
          userId: refreshTokenPayload.userId,
          token: localStorage.refreshToken,
          ip: localStorage.ip
        }).then(response => {
          const tokens = response.bodyText.split(' ');
          localStorage.accessToken = tokens[0];
          localStorage.refreshToken = tokens[1];

          const newAccessTokenPayload = JSON.parse(atob(localStorage.accessToken.split('.')[1]));
          this.$store.commit('sidebar/changeUsername', newAccessTokenPayload.sub);
          this.$store.commit('sidebar/changeAuthorized', true);
        });
      },

      logout() {
        if (localStorage.refreshToken) {
          const refreshTokenPayload = JSON.parse(atob(localStorage.refreshToken.split('.')[1]));
          this.$http.post('/api/logout', {
            userId: refreshTokenPayload.userId,
            ip: localStorage.ip
          })
            .then(() => {
              localStorage.clear();
              this.$store.commit('sidebar/changeAuthorized', false);
              this.$router.push('/login');
            });
        }
      }
    },

    computed: {
      ...mapState({
        username: state => state.sidebar.username,
        authorized: state => state.sidebar.authorized
      })
    },

    mounted: function() {
      if (localStorage.accessToken) {
        const accessTokenPayload = JSON.parse(atob(localStorage.accessToken.split('.')[1]));

        if ((accessTokenPayload.exp - 100) + '000' < Date.now()) {
          this.refreshTokens();
        } else {
          this.$store.commit('sidebar/changeUsername', accessTokenPayload.sub);
          this.$store.commit('sidebar/changeAuthorized', true);
        }

        this.userRoles = JSON.parse(localStorage.getItem('roles'));
        this.$store.dispatch('websocket/connect');
      }

      this.intervalFunction = setInterval(() => {
        if (localStorage.accessToken) {
          const accessTokenPayload = JSON.parse(atob(localStorage.accessToken.split('.')[1]));
          if ((accessTokenPayload.exp - 100) + '000' < Date.now()) {
            this.refreshTokens();
          }
        }
      }, 120000);
    }
  };
</script>

<style scoped>
  .md-card.user-profile-card {
    margin: 5px;
  }
</style>
