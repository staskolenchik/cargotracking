<template>
  <div>
    <transition name="collapse">
      <invoice-filter-content v-if="showFilters" />
    </transition>

    <md-table
      :value="invoices"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Invoice List</h1>
        <md-button
          v-if="userRoles.includes('DISPATCHER')"
          class="md-icon-button
          md-raised
          md-primary"
          to="/invoices/add"
        >
          <md-icon title="add invoice">add</md-icon>
        </md-button>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          @click="showFilters = !showFilters"
        >
          <md-icon title="search invoice">search</md-icon>
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
        :md-disabled="item.status === 'Delivered'"
      >
        <md-table-cell
          md-label="Number"
          md-sort-by="number"
        >
          {{ item.number }}
        </md-table-cell>

        <md-table-cell
          md-label="Creation Date"
          md-sort-by="creationDate"
        >
          {{ item.creationDate }}
        </md-table-cell>

        <md-table-cell
          md-label="Verified Date"
          md-sort-by="verifiedDate"
        >
          {{ item.verifiedDate }}
        </md-table-cell>

        <md-table-cell
          md-label="Status"
          md-sort-by="status"
        >
          {{ item.status }}
        </md-table-cell>

        <md-table-cell
          md-label="Operation"
          class="operation-column"
        >
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            v-if="(userRoles.includes('DISPATCHER') && item.status === 'Made out') ||
            (userRoles.includes('MANAGER') && item.status === 'Delivered') ||
            (userRoles.includes('MANAGER') && item.status === 'Made out') ||
            (userRoles.includes('COMPANY_OWNER'))"
            @click="getInvoice(item.id)"
          >
            <md-icon
              v-if="(userRoles.includes('DISPATCHER') && item.status === 'Made out') ||
              (userRoles.includes('MANAGER') && item.status === 'Delivered')"
              title="edit invoice"
            >
              edit
            </md-icon>
            <md-icon
              v-else-if="userRoles.includes('MANAGER') && item.status === 'Made out'"
              title="verify invoice"
            >
              check
            </md-icon>
            <md-icon
              v-else-if="userRoles.includes('COMPANY_OWNER')"
              title="show invoice"
            >
              visibility
            </md-icon>
          </md-button>
          <md-button
            v-if="(userRoles.includes('DISPATCHER') && item.status === 'Made out') ||
            (userRoles.includes('MANAGER') && item.status === 'Delivered')"
            class="md-icon-button
            md-dense md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete invoice">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No invoices found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageInvoiceChange"
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
      md-content="Invoices has been deleted!"
      md-confirm-text="OK"
    />
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  import InvoiceFilterContent from './InvoiceFilterContent';
  import {Confirmation} from '../../../constants/confirmation';
  import {Url} from '../../../constants/url';

  export default {
    name: 'InvoiceListContent',
    data: () => ({
      showFilters: false,
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete invoice',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      currentSort: 'creationDate',
      currentSortOrder: 'desc',

      hasDeleted: false,
      isMounted: false,

      userRoles: []
    }),
    methods: {
      ...mapActions('invoice', [
        'pageInvoiceChange'
      ]),

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.INVOICE, {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('invoice/pageInvoiceChange', {
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
        return `${count} invoice${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      customSort() {
        this.$store.commit('invoice/setSort', `${this.currentSort},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('invoice/pageInvoiceChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      getInvoice(id) {
        this.$router.push({name: 'get-invoice', params: {id}});
      }
    },

    mounted: function() {
      this.userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!this.userRoles || !this.userRoles.includes('DISPATCHER')
          && !this.userRoles.includes('COMPANY_OWNER')
          && !this.userRoles.includes('MANAGER')
      ) {
        this.$router.replace('/');
      }
      this.isMounted = true;
    },

    computed: {
      ...mapState({
        totalElements: state => state.invoice.totalElements,
        invoices: state => state.invoice.items
      })
    },

    components: {InvoiceFilterContent}
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
