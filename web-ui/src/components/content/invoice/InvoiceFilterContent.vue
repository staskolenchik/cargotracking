<template>
  <div class="p-20">
    <md-field>
      <label>Number</label>
      <md-input v-model="number"></md-input>
    </md-field>

    <h3>Creation Date</h3>
    <md-datepicker v-model="beforeCreationDate" md-immediately>
      <label>Before</label>
    </md-datepicker>

    <md-datepicker v-model="afterCreationDate" md-immediately>
      <label>After</label>
    </md-datepicker>

    <h3>Verified Date</h3>
    <md-datepicker v-model="beforeVerifiedDate" md-immediately>
      <label>Before</label>
    </md-datepicker>

    <md-datepicker v-model="afterVerifiedDate" md-immediately>
      <label>After</label>
    </md-datepicker>

    <h3>Status</h3>
    <md-checkbox v-model="statuses" value="MADE_OUT">Made out</md-checkbox>
    <md-checkbox v-model="statuses" value="VERIFICATION_COMPLETE">Verified</md-checkbox>
    <md-checkbox v-model="statuses" value="DELIVERED">Delivered</md-checkbox>

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
    name: 'InvoiceFilterContent',

    methods: {
      find() {
        this.$store.dispatch('invoice/pageInvoiceChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      removeFilters() {
        this.$store.commit('invoice/removeFilter');
        this.find();
      }
    },

    computed: {
      number: {
        get() {
          return this.$store.state.invoice.filter.number;
        },
        set(value) {
          this.$store.commit('invoice/updateNumber', value);
        }
      },
      beforeCreationDate: {
        get() {
          return this.$store.state.invoice.filter.beforeCreationDate;
        },
        set(value) {
          this.$store.commit('invoice/updateBeforeCreationDate', value);
        }
      },
      afterCreationDate: {
        get() {
          return this.$store.state.invoice.filter.afterCreationDate;
        },
        set(value) {
          this.$store.commit('invoice/updateAfterCreationDate', value);
        }
      },
      beforeVerifiedDate: {
        get() {
          return this.$store.state.invoice.filter.beforeVerifiedDate;
        },
        set(value) {
          this.$store.commit('invoice/updateBeforeVerifiedDate', value);
        }
      },
      afterVerifiedDate: {
        get() {
          return this.$store.state.invoice.filter.afterVerifiedDate;
        },
        set(value) {
          this.$store.commit('invoice/updateAfterVerifiedDate', value);
        }
      },
      statuses: {
        get() {
          return this.$store.state.invoice.filter.statuses;
        },
        set(value) {
          this.$store.commit('invoice/updateStatuses', value);
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
