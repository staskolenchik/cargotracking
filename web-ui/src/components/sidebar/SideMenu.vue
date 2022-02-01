<template>
  <md-list>
    <md-toolbar md-elevation="0">
      <h3 class="md-title">Menu</h3>
    </md-toolbar>

    <md-list-item
      v-if="userRoles.includes('SYS_ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/clients">
          Client companies
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/storages">
          Storages
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/product-owners">
          Product owners
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('DISPATCHER') ||
      userRoles.includes('MANAGER') ||
      userRoles.includes('COMPANY_OWNER')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/invoices">
          Invoices
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('MANAGER') ||
      userRoles.includes('DRIVER') ||
      userRoles.includes('COMPANY_OWNER')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/waybills">
          Waybills
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/users">
          Users
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/cars">
          Cars
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/email">
          Email
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('COMPANY_OWNER')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/reports/client">
          Report
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/template">
          Birthday template
        </router-link>
      </span>
    </md-list-item>

    <md-list-item
      v-if="userRoles.includes('SYS_ADMIN')"
    >
      <md-icon>send</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/reports/sysadmin">
          Statistic
        </router-link>
      </span>
    </md-list-item>

    <md-list-item>
      <md-icon>info</md-icon>
      <span
        class="md-list-item-text"
        @click="hideBar"
      >
        <router-link to="/about">
            About
        </router-link>
      </span>
    </md-list-item>
  </md-list>
</template>

<script>
  export default {
    name: 'SideMenu',

    data: () => ({
      userRoles: []
    }),

    methods: {
      hideBar() {
        this.$store.commit('sidebar/changeVisibility', false);
      }
    },

    mounted: function() {
      const roles = JSON.parse(localStorage.getItem('roles'));
      if (roles) {
        this.userRoles = roles;
      }
    }
  };
</script>

<style scoped>

</style>
