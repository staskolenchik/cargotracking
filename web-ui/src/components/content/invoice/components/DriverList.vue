<template>
  <div>
    <md-table
      :value="drivers"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Driver List</h1>
        <md-card-actions>
          <md-button
            class="md-primary"
            @click="showMainForm"
          >
            Back
          </md-button>
        </md-card-actions>
      </md-table-toolbar>

      <md-table-row
        slot="md-table-row"
        slot-scope="{ item }"
        md-selectable="single"
      >
        <md-table-cell
          md-label="Full name"
          md-sort-by="surname"
        >
          {{ item.name }} {{item.surname}} {{item.patronymic}}
        </md-table-cell>
        <md-table-cell
          md-label="Login"
          md-sort-by="login"
        >
          {{ item.login }}
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No drivers found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageUsersChange"
    >
    </v-page>
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  export default {
    name: 'UserList',

    methods: {
      ...mapActions('user', [
        'pageUsersChange'
      ]),

      search() {
        this.$store.dispatch('user/pageUsersChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      onSelect(item) {
        this.$store.commit('invoice/updateDataDriver', item);
        this.showMainForm();
      },

      showMainForm() {
        this.$store.commit('user/updateUserRoles', []);
        this.$store.commit('invoice/showMainForm');
      }
    },

    computed: {
      ...mapState({
        totalElements: state => state.user.totalElements,
        drivers: state => state.user.items
      })
    },

    mounted: function() {
      this.$store.commit('user/setSort', 'name,asc');
    }
  };
</script>

<style scoped>
  .md-table {
    overflow-x: auto;
  }

  .v-pagination {
    margin-top: 20px;
  }

  .md-card-actions {
    padding: 0 15px;
  }
</style>
