<template>
  <div class="p-20">
    <md-field>
      <label>Number</label>
      <md-input v-model="number"></md-input>
    </md-field>
    <h3>Load capacity</h3>
    <md-field>
      <label>Less then</label>
      <md-input v-model="loadCapacityLess"></md-input>
    </md-field>
    <md-field>
      <label>More then</label>
      <md-input v-model="loadCapacityMore"></md-input>
    </md-field>
    <h3>Fuel consumption</h3>
    <md-field>
      <label>Less then</label>
      <md-input v-model="fuelConsumptionLess"></md-input>
    </md-field>
    <md-field>
      <label>More then</label>
      <md-input v-model="fuelConsumptionMore"></md-input>
    </md-field>
    <h3>Car type</h3>
    <md-checkbox v-model="carTypes" value="CISTERN">Cistern</md-checkbox>
    <md-checkbox v-model="carTypes" value="REFRIGERATOR">Refrigerator</md-checkbox>
    <md-checkbox v-model="carTypes" value="COVERED_BODY">Covered body</md-checkbox>
    <md-card-actions>
      <md-button class="md-primary" @click="removeFilters">
        Remove filters
      </md-button>
      <md-button class="md-primary" @click="find">
        Search
      </md-button>
    </md-card-actions>
  </div>
</template>

<script>
  export default {
    name: 'CarFilterContent',

    methods: {
      find() {
        this.$store.dispatch('car/pageCarChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },
      removeFilters() {
        this.$store.commit('car/removeFilter');

        this.find();
      }
    },

    computed: {
      number: {
        get() {
          return this.$store.state.car.filter.number;
        },
        set(value) {
          this.$store.commit('car/updateFilterNumber', value);
        }
      },
      fuelConsumptionLess: {
        get() {
          return this.$store.state.car.filter.fuelConsumptionLess;
        },
        set(value) {
          this.$store.commit('car/updateFilterFuelConsumptionLess', value);
        }
      },
      fuelConsumptionMore: {
        get() {
          return this.$store.state.car.filter.fuelConsumptionMore;
        },
        set(value) {
          this.$store.commit('car/updateFilterFuelConsumptionMore', value);
        }
      },
      loadCapacityLess: {
        get() {
          return this.$store.state.car.filter.loadCapacityLess;
        },
        set(value) {
          this.$store.commit('car/updateFilterLoadCapacityLess', value);
        }
      },
      loadCapacityMore: {
        get() {
          return this.$store.state.car.filter.loadCapacityMore;
        },
        set(value) {
          this.$store.commit('car/updateFilterLoadCapacityMore', value);
        }
      },
      carTypes: {
        get() {
          return this.$store.state.car.filter.carTypes;
        },
        set(value) {
          this.$store.commit('car/updateFilterCarTypes', value);
        }
      }
    }
  };
</script>

<style scoped>
  .p-20 {
    padding: 20px;
  }
</style>
