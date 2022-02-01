<template>
  <div>
    <transition name="collapse">
      <user-filter-content v-if="showFilters" />
    </transition>

    <md-table
      :value="users"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">User List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/users/add"
        >
          <md-icon title="add user">add</md-icon>
        </md-button>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          @click="showFilters = !showFilters"
        >
          <md-icon title="search users">search</md-icon>
        </md-button>
      </md-table-toolbar>

      <md-table-toolbar slot="md-table-alternate-header" slot-scope="{ count }">

        <div class="md-toolbar-section-start">{{ getAlternateLabel(count) }}</div>
        <div class="md-toolbar-section-end">
          <md-button class="md-icon-button" @click="deleteUsers(selectedIds)">
            <md-icon>delete</md-icon>
          </md-button>
        </div>
      </md-table-toolbar>

      <md-table-row
        slot="md-table-row"
        slot-scope="{ item }"
        md-selectable="multiple"
        :md-disabled="item.userRoles.includes('Admin')"
      >
        <md-table-cell
          md-label="Full Name"
          md-sort-by="surname"
        >
          {{ item.name }} {{item.surname}} {{item.patronymic}}
        </md-table-cell>

        <md-table-cell
          md-label="Login"
          md-sort-by="login"
        >
          {{item.login}}
        </md-table-cell>

        <md-table-cell
          md-label="User Roles"
        >
          {{item.userRoles.toString()}}
        </md-table-cell>

        <md-table-cell md-label="Operation" class="operation-column">
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="getUser(item.id)"
          >
            <md-icon title="edit user">edit</md-icon>
          </md-button>
          <md-button
            v-if="!item.userRoles.includes('Admin')"
            class="md-icon-button
            md-dense md-primary"
            @click="deleteUsers(item.id)"
          >
            <md-icon title="delete user">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No users found" />
    </md-table>
    <v-page
      :total-row="totalElements"
      @page-change="pageUsersChange"
    >
    </v-page>
    <md-dialog-confirm
      :md-active.sync="activeConfirmDialog"
      :md-title="tittleConfirmDialog"
      :md-content="contentConfirmDialog"
      md-confirm-text="Delete"
      md-cancel-text="Cancel"
      @md-cancel="onCancel"
      @md-confirm="onDeleteConfirm"
    />
    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Users has been deleted!"
      md-confirm-text="OK"
    />
  </div>
</template>
<script>
  import {mapState, mapActions} from 'vuex';

  import {Url} from '../../../constants/url';
  import UserFilterContent from './UserFilterContent';

  export default {
    name: 'UserListContent',

    data: () => ({
      showFilters: false,
      selectedRows: [],
      selectedIds: [],
      itemIdsToDelete: [],
      activeConfirmDialog: false,
      tittleConfirmDialog: 'Delete users',
      contentConfirmDialog: 'Do you really want to delete item?',
      currentSort: 'login',
      currentSortOrder: 'asc',
      hasDeleted: false,
      isMounted: false
    }),

    methods: {
      ...mapActions('user', [
        'pageUsersChange'
      ]),

      deleteUsers(idToDelete){
        this.activeConfirmDialog = true;
        if (Array.isArray(idToDelete)) {
          this.itemIdsToDelete = idToDelete;
        } else {
          this.itemIdsToDelete.push(idToDelete);
        }
      },

      onCancel(){
        this.removeSelection();
        this.itemIdsToDelete = [];
      },

      onDeleteConfirm(){
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.USER, {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('user/pageUsersChange', {
              pageNumber: 1,
              pageSize: 10
            });
          });
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      onSelect (items) {
        this.selectedRows = items;
        this.selectedIds = items.map((item)=>item.id);
      },

      getAlternateLabel (count) {
        const plural = count > 1 ? 's' : '';
        return `${count} user${plural} selected`;
      },

      customSort() {
        this.$store.commit('user/setSort', `${this.currentSort},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('user/pageUsersChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      getUser(id) {
        this.$router.push({name: 'get-user', params: {id}});
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('ADMIN')) {
        this.$router.replace('/');
      }

      this.isMounted = true;
    },

    computed: {
      ...mapState({
        totalElements: state => state.user.totalElements,
        users: state => state.user.items
      })
    },

    components: {UserFilterContent}
  };
</script>

<style scoped>
  .operation-column {
    width: 150px;
  }

  .md-table {
    overflow-x: auto;
  }

  .collapse-enter-active, .collapse-leave-active {
    transition: all .5s;
  }
  .collapse-enter, .collapse-leave-to {
    transform: translateY(-100px);
    opacity: 0;
  }
</style>
