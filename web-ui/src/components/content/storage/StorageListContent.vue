<template>
  <div>
    <md-card-actions>
      <md-field md-clearable>
        <md-input placeholder="Search by name..." v-model="name"/>
      </md-field>
      <md-button
        class="md-icon-button
        md-raised
        md-primary"
        @click="search"
      >
        <md-icon title="search storage">search</md-icon>
      </md-button>
    </md-card-actions>

    <md-table
      :value="storages"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Storage List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/storages/add"
        >
          <md-icon title="add storage">add</md-icon>
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
      >
        <md-table-cell
          md-label="Name"
          md-sort-by="name"
        >
          {{ item.name }}
        </md-table-cell>

        <md-table-cell
          md-label="Address"
          md-sort-by="address"
        >
          {{ item.address }}
        </md-table-cell>

        <md-table-cell md-label="Operation" class="operation-column">
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="getStorage(item.id)"
          >
            <md-icon title="edit storage">edit</md-icon>
          </md-button>
          <md-button
            class="md-icon-button
            md-dense md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete storage">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No storages found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageStorageChange"
    >
    </v-page>

    <md-dialog-confirm
      :md-active.sync="activeConfirmDialog"
      :md-title="tittleConfirmDialog"
      :md-content="contentConfirmDialog"
      md-confirm-text="Delete"
      md-cancel-text="Cancel"
      @md-cancel="onCancel"
      @md-confirm="onDeleteConfirm"/>

    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Storage has been deleted!"
      md-confirm-text="OK"/>
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  import {Confirmation} from '../../../constants/confirmation';
  import {Url} from '../../../constants/url';

  export default {
    name: 'StorageListContent',

    methods: {
      ...mapActions('storage', [
        'pageStorageChange'
      ]),

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.STORAGE, {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('storage/pageStorageChange', {
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
        return `${count} storage${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      customSort() {
        let suffix = '';
        if (this.currentSort === 'name' || this.currentSort === 'address') {
          suffix = '_str';
        }

        this.$store.commit('storage/setSort', `${this.currentSort}${suffix},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('storage/pageStorageChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      search() {
        this.$store.dispatch('storage/pageStorageChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      getStorage(id) {
        this.$router.push({name: 'get-storage', params: {id}});
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
        totalElements: state => state.storage.totalElements,
        storages: state => state.storage.items
      }),
      name: {
        get() {
          return this.$store.state.storage.filter.name;
        },
        set(value) {
          this.$store.commit('storage/updateFilterName', value);
        }
      }
    },

    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete storage',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      currentSort: 'name',
      currentSortOrder: 'asc',
      hasDeleted: false,
      isMounted: false
    })
  };
</script>

<style scoped>
  .md-table {
    overflow-x: auto;
  }

  .operation-column {
    width: 150px;
  }
</style>

