<template>
  <div>
    <md-table
      :value="waybills"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Waybill List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/waybills/add"
        >
          <md-icon title="add waybill">add</md-icon>
        </md-button>
      </md-table-toolbar>

      <md-table-toolbar
        v-if="userRoles.includes('MANAGER')"
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
          slot-scope="{item}"
          md-selectable="multiple"
          :md-disabled="!userRoles.includes('MANAGER') || item.status === 'Started carriage'"
        >
          <md-table-cell
            md-label="Sender"
            md-sort-by="sender"
          >
            {{item.sender}}
          </md-table-cell>

          <md-table-cell
            md-label="Receiver"
            md-sort-by="receiver"
          >
            {{item.receiver}}
          </md-table-cell>

          <md-table-cell
            md-label="Invoice Number"
            md-sort-by="invoiceNumber"
            >
            {{item.invoiceNumber}}
          </md-table-cell>

          <md-table-cell
            md-label="Car number"
            md-sort-by="carNumber"
          >
            {{item.carNumber}}
          </md-table-cell>

          <md-table-cell
            md-label="Registration Date"
            md-sort-by="startDate"
          >
            {{item.startDate}}
          </md-table-cell>

          <md-table-cell
            md-label="Status"
            md-sort-by="status"
          >
            {{item.status}}
          </md-table-cell>

          <md-table-cell
            md-label="Operation"
            class="operation-column"
          >
            <md-button
              v-if="userRoles.includes('MANAGER') && item.status !== 'Started carriage'"
              class="md-icon-button
              md-dense md-primary"
              @click="showDeleteConfirmDialog(item.id)"
            >
              <md-icon title="delete waybill">delete</md-icon>
            </md-button>

            <md-button
              v-if="userRoles.includes('MANAGER') ||
              userRoles.includes('COMPANY_OWNER') ||
              userRoles.includes('DRIVER')"
              class="md-icon-button
              md-dense md-primary"
              @click="getWaybill(item.id)"
            >
              <md-icon title="show waybill">visibility</md-icon>
            </md-button>
          </md-table-cell>

        </md-table-row>

      <md-table-empty-state md-label="No waybills found" />

    </md-table>

    <v-page
      class="v-pagination"
      :total-row="totalElements"
      @page-change="pageWaybillChange"
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
      md-content="Waybills has been deleted!"
      md-confirm-text="OK"
    />

    <md-snackbar :md-active.sync="waybillSaved">
      {{messages.WAYBILL_SAVED}}
    </md-snackbar>
  </div>
</template>

<script>
  import {mapActions, mapState} from 'vuex';

  import {Confirmation} from '../../../constants/confirmation';
  import {Url} from '../../../constants/url';
  import {Messages} from '../../../constants/messages';

  export default {
    name: 'WaybillListContent',

    data: () => ({
      showFilters: false,
      currentSort: 'startDate',
      currentSortOrder: 'desc',
      messages: Messages,

      selectedRows: [],
      selectedIds: [],

      itemIdsToDelete: [],
      activeConfirmDialog: false,
      tittleConfirmDialog: 'Delete waybill',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      hasDeleted: false,
      isMounted: false,

      userRoles: []
    }),

    computed: {
      ...mapState({
        totalElements: state => state.waybill.totalElements,
        waybills: state => state.waybill.items
      }),
      waybillSaved: {
        get() {
          return this.$store.state.waybill.waybillSaved;
        },
        set(value) {
          this.$store.commit('waybill/setWaybillSaved', value);
        }
      }
    },

    methods: {
      ...mapActions('waybill', [
        'pageWaybillChange'
      ]),

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.WAYBILL, {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('waybill/pageWaybillChange', {
              pageNumber: 1,
              pageSize: 10
            });
          });
      },

      getAlternateLabel(count) {
        const plural = count > 1 ? 's' : '';
        return `${count} waybill${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
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

      getWaybill(id) {
        this.$router.push({name: 'get-waybill', params: {id}});
      },

      customSort() {
        this.$store.commit('waybill/setSort', `${this.currentSort},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('waybill/pageWaybillChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      }
    },

    mounted: function() {
      this.userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!this.userRoles || !this.userRoles.includes('MANAGER')
          && !this.userRoles.includes('DRIVER')
          && !this.userRoles.includes('COMPANY_OWNER')
      ) {
        this.$router.replace('/');
      }
      this.isMounted = true;
    }
  };
</script>

<style scoped>
  .md-table {
    overflow-x: auto;
  }

  .operation-column {
    width: 150px;
  }

  .v-pagination {
    margin-top: 20px;
  }
</style>
