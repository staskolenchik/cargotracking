<template>
  <div>
    <transition name="collapse">
      <car-filter-content v-if="showFilters" />
    </transition>

    <md-table
      :value="cars"
      :md-sort.sync="currentSort"
      :md-sort-order.sync="currentSortOrder"
      :md-sort-fn="customSort"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Car List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          to="/cars/add"
        >
          <md-icon title="add car">add</md-icon>
        </md-button>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          @click="showFilters = !showFilters"
        >
          <md-icon title="search car">search</md-icon>
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
          md-label="Number"
          md-sort-by="number"
        >
          {{ item.number }}
        </md-table-cell>
        <md-table-cell
          md-label="Load capacity"
          md-sort-by="loadCapacity"
        >
          {{ item.loadCapacity }}
        </md-table-cell>
        <md-table-cell
          md-label="Car type"
          md-sort-by="carType"
        >
          {{ item.carType }}
        </md-table-cell>
        <md-table-cell md-label="Operation" class="operation-column">
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="getCar(item.id)"
          >
            <md-icon title="edit car">edit</md-icon>
          </md-button>
          <md-button
            class="md-icon-button
            md-dense md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete car">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No cars found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageCarChange"
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
      md-content="Cars has been deleted!"
      md-confirm-text="OK"/>
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  import CarFilterContent from './CarFilterContent';
  import {Confirmation} from '../../../constants/confirmation';
  import {Url} from '../../../constants/url';

  export default {
    name: 'CarListContent',

    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete car',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      currentSort: 'number',
      currentSortOrder: 'asc',
      hasDeleted: false,
      showFilters: false,
      isMounted: false
    }),

    methods: {
      ...mapActions('car', [
        'pageCarChange'
      ]),

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.CAR, {
            body: itemIdsToDelete,
            headers: {
              Authorization: `Bearer ${localStorage.accessToken}`
            }
          }
        )
          .then(() => {
            this.hasDeleted = true;
            this.itemIdsToDelete = [];
            this.$store.dispatch('car/pageCarChange', {
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
        return `${count} car${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      customSort() {
        this.$store.commit('car/setSort', `${this.currentSort},${this.currentSortOrder}`);
        if (this.isMounted) {
          this.$store.dispatch('car/pageCarChange', {
            pageNumber: 1,
            pageSize: 10
          });
        }
      },

      search() {
        this.$store.dispatch('car/pageCarChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      getCar(id) {
        this.$router.push({name: 'get-car', params: {id}});
      }
    },

    mounted: function() {
      this.$store.commit('car/updateFilterLoadCapacityMore', null);

      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('ADMIN')) {
        this.$router.replace('/');
      }

      this.isMounted = true;
    },

    computed: {
      ...mapState({
        totalElements: state => state.car.totalElements,
        cars: state => state.car.items
      })
    },

    components: {CarFilterContent}
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
