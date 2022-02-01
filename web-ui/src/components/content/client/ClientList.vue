<template>
  <div>
    <transition name="collapse">
      <client-list-filter v-if="showFilters"/>
    </transition>

    <md-table
      :value="clients"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Company List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/clients/add"
        >
          <md-icon title="add company">add</md-icon>
        </md-button>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          @click="showFilters = !showFilters"
        >
          <md-icon title="search company">search</md-icon>
        </md-button>
      </md-table-toolbar>

      <md-table-toolbar
        slot="md-table-alternate-header"
        slot-scope="{ count }"
      >
        <div class="md-toolbar-section-start">{{ getAlternateLabel(count) }}</div>

        <div class="md-toolbar-section-end">
          <md-button class="md-icon-button" @click="showDeleteConfirmDialog(selectedIds)">
            <md-icon>delete</md-icon>
          </md-button>
        </div>
      </md-table-toolbar>

      <md-table-row
        slot="md-table-row"
        slot-scope="{ item }"
        md-selectable="multiple"
        :md-disabled="item.deleteDate !== null"
      >
        <md-table-cell
          md-label="Company name"
          md-sort-by="name"
        >
          {{ item.name }}
        </md-table-cell>

        <md-table-cell
          md-label="Company type"
          md-sort-by="status"
        >
          {{ item.status }}
        </md-table-cell>

        <md-table-cell md-label="Operation" class="operation-column">
          <md-button
            v-if="item.deleteDate === null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="getClient(item.id)"
          >
            <md-icon title="edit company">edit</md-icon>
          </md-button>

          <md-button
            v-if="item.deleteDate === null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete company">delete</md-icon>
          </md-button>

          <md-button
            v-if="item.deleteDate !== null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="showActivateConfirmDialog(item)"
          >
            <md-icon title="activate company">rotate_left</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No companies found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageClientChange"
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

    <md-dialog-confirm
      :md-active.sync="activeActivateDialog"
      :md-title="tittleActivateDialog"
      :md-content="contentActivateDialog"
      md-confirm-text="Activate"
      md-cancel-text="Cancel"
      @md-confirm="activateClient"
    />

    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Companies has been deleted!"
      md-confirm-text="OK"
    />

    <md-dialog-alert
      :md-active.sync="hasActivated"
      md-content="Company was activated successfully!"
      md-confirm-text="OK"
    />
  </div>
</template>

<script>
  import {mapActions, mapState} from 'vuex';

  import ClientListFilter from './ClientListFilter';

  export default {
    name: 'ClientList',

    components: {ClientListFilter},

    data: () => ({
      selectedRows: [],
      selectedIds: [],

      clientForActivate: null,

      activeActivateDialog: false,
      tittleActivateDialog: 'Activate company',
      contentActivateDialog: 'Do you really want to activate company?',
      hasActivated: false,

      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete company',
      contentConfirmDialog: 'Do you really want to delete item?',
      hasDeleted: false,

      currentSort: 'name',
      currentSortOrder: 'asc',

      showFilters: false,
      isMounted: false
    }),

    methods: {
      ...mapActions('client', [
        'pageClientChange'
      ]),

      showActivateConfirmDialog(item) {
        this.activeActivateDialog = true;
        this.clientForActivate = item;
      },

      activateClient() {
        this.$http.put(`/api/clients/activate/${this.clientForActivate.id}`, {}, {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasActivated = true;
            this.clientForActivate.deleteDate = null;
          });
      },

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete('/api/clients', {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('client/pageClientChange', {
              pageNumber: 1,
              pageSize: 10
            });
          });
      },

      onCancel() {
        this.removeSelection();
        this.itemIdsToDelete = [];
      },

      showDeleteConfirmDialog(idToDelete) {
        this.activeConfirmDialog = true;
        if (Array.isArray(idToDelete)) {
          this.itemIdsToDelete = idToDelete;
        } else {
          this.itemIdsToDelete.push(idToDelete);
        }
      },

      onSelect(items) {
        this.selectedRows = items;
        this.selectedIds = items.map((item) => item.id);
      },

      getAlternateLabel(count) {
        const plural = count > 1 ? 's' : '';
        return `${count} client${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      customSort() {
        this.$store.commit('client/setSort', `${this.currentSort},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('client/pageClientChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      getClient(id) {
        this.$router.push({name: 'get-client', params: {id}});
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('SYS_ADMIN')) {
        this.$router.replace('/');
      }

      this.isMounted = true;
    },

    computed: {
      ...mapState({
        totalElements: state => state.client.totalElements,
        clients: state => state.client.items
      })
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

  .operation-column {
    width: 150px;
  }

  .collapse-enter-active, .collapse-leave-active {
    transition: all .5s;
  }

  .collapse-enter, .collapse-leave-to {
    transform: translateY(-100px);
    opacity: 0;
  }
</style>
