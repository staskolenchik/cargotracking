<template>
  <div>
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
      </md-table-row>

      <md-table-empty-state md-label="No cars found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageCarChange"
    >
    </v-page>

  </div>
</template>

<script>
  import {mapActions, mapState} from 'vuex';

  export default {
    name: 'CarList',

    data: () => ({
      currentSort: 'number',
      currentSortOrder: 'asc'
    }),

    methods: {
      ...mapActions('car', [
        'pageCarChange'
      ]),

      onSelect(item) {
        this.$store.commit('waybill/updateDataCar', item);
        this.showMainForm();
      },

      showMainForm() {
        this.$store.commit('waybill/showMainForm');
      },

      customSort() {
        this.$store.commit('car/updateFilterLoadCapacityMore', this.$store.state.waybill.data.invoice.load);
        this.$store.commit('car/setSort', `${this.currentSort},${this.currentSortOrder}`);
        this.$store.dispatch('car/pageCarChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      search() {
        this.$store.dispatch('car/pageCarChange', {
          pageNumber: 1,
          pageSize: 10
        });
      }
    },

    computed: {
      ...mapState({
        totalElements: state => state.car.totalElements,
        cars: state => state.car.items
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
