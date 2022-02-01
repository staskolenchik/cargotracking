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
        <md-icon title="search product owner">search</md-icon>
      </md-button>
    </md-card-actions>

    <md-table
      :value="productOwners"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Product Owner List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/product-owners/add"
        >
          <md-icon title="add product owner">add</md-icon>
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
            @click="getProductOwner(item.id)"
          >
            <md-icon title="edit product owner">edit</md-icon>
          </md-button>
          <md-button
            class="md-icon-button
            md-dense md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete product owner">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No product owners found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageProductOwnerChange"
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
      md-content="Product owners has been deleted!"
      md-confirm-text="OK"/>
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  import {Confirmation} from '../../../constants/confirmation';
  import {Url} from '../../../constants/url';

  export default {
    name: 'ProductOwnerListContent',
    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete product owner',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      currentSort: 'name',
      currentSortOrder: 'asc',

      hasDeleted: false,
      isMounted: false
    }),

    methods: {
      ...mapActions('productOwner', [
        'pageProductOwnerChange'
      ]),

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.PRODUCT_OWNER, {
            body: itemIdsToDelete,
            headers: {
              Authorization: `Bearer ${localStorage.accessToken}`
            }
          }
        )
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('productOwner/pageProductOwnerChange', {
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
        return `${count} product owner${plural} selected`;
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

        this.$store.commit('productOwner/setSort', `${this.currentSort}${suffix},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('productOwner/pageProductOwnerChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      search() {
        this.$store.dispatch('productOwner/pageProductOwnerChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      getProductOwner(id) {
        this.$router.push({name: 'get-product-owner', params: {id}});
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
        totalElements: state => state.productOwner.totalElements,
        productOwners: state => state.productOwner.items,
        name: state => state.productOwner.filter.name
      }),
      name: {
        get() {
          return this.$store.state.productOwner.filter.name;
        },
        set(value) {
          this.$store.commit('productOwner/updateFilterName', value);
        }
      }
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

  .md-card-actions {
    padding: 0 15px;
  }
</style>
