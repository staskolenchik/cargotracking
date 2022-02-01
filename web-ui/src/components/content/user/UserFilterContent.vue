<template>
  <div class="p-20">
    <md-field>
      <label>Surname</label>
      <md-input v-model="surname"></md-input>
    </md-field>

    <md-field>
      <label>User Roles</label>
      <md-select v-model="userRoles" multiple>
        <md-option value="DISPATCHER">Dispatcher</md-option>
        <md-option value="MANAGER">Manager</md-option>
        <md-option value="DRIVER">Driver</md-option>
        <md-option value="COMPANY_OWNER">Company Owner</md-option>
      </md-select>
    </md-field>

    <md-card-actions>
      <md-button class="md-primary" @click="removeFilters">
        Remove filters
      </md-button>
      <md-button class="md-primary" @click="find">
        Search
      </md-button>
    </md-card-actions>
  </div>
</template>

<script>
  export default {
    name: 'UserFilterContent',

    methods: {
      find() {
        this.$store.dispatch('user/pageUsersChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      removeFilters() {
        this.$store.commit('user/removeFilter');
        this.find();
      }
    },

    computed: {
      surname: {
        get() {
          return this.$store.state.user.filter.surname;
        },
        set(value) {
          this.$store.commit('user/updateSurname', value);
        }
      },
      userRoles: {
        get() {
          return this.$store.state.user.filter.userRoles;
        },
        set(value) {
          this.$store.commit('user/updateUserRoles', value);
        }
      }
    }
  };
</script>

<style scoped>

</style>
