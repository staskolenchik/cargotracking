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
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Storage List</h1>
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
      </md-table-row>

      <md-table-empty-state md-label="No storages found" />
    </md-table>

    <v-page
      :total-row="totalElements"
      @page-change="pageStorageChange"
    >
    </v-page>
  </div>
</template>

<script>
  import {mapState, mapActions} from 'vuex';

  export default {
    name: 'StorageList',

    methods: {
      ...mapActions('storage', [
        'pageStorageChange'
      ]),

      search() {
        this.$store.dispatch('storage/pageStorageChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      onSelect(item) {
        this.$store.commit('invoice/updateDataStorage', item);
        this.showMainForm();
      },

      showMainForm() {
        this.$store.commit('invoice/showMainForm');
      }
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

    mounted: function() {
      this.$store.commit('storage/setSort', 'name_str,asc');
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
