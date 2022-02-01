<template>
  <div class="clientSearch">
    <h2>Search:</h2>
    <md-field>
      <label>Company name</label>
      <md-input v-model="name"></md-input>
    </md-field>
    <h3>Company type</h3>
    <md-checkbox v-model="status" value="PRIVATE">Private</md-checkbox>
    <md-checkbox v-model="status" value="LEGAL">Legal</md-checkbox>
    <md-card-actions>
      <md-button class="md-accent" @click="removeFilter">
        Remove filter
      </md-button>
      <md-button class="md-primary" @click="find">
        Search
      </md-button>
    </md-card-actions>
  </div>
</template>

<script>
  export default {
    name: 'ClientListFilter',

    methods: {
      find() {
        this.$store.dispatch('client/pageClientChange', {
          pageNumber: 1,
          pageSize: 10
        });
      },

      removeFilter() {
        this.$store.commit('client/removeFilter');
        this.find();
      }
    },

    computed: {
      name: {
        get() {
          return this.$store.state.client.filter.name;
        },
        set(value) {
          this.$store.commit('client/updateName', value);
        }
      },
      status:{
        get(){
          return this.$store.state.client.filter.status;
        },
        set(value){
          this.$store.commit('client/updateStatus',value);
        }
      }
    }
  };
</script>

<style scoped>
  .clientSearch{
    padding: 20px;
  }
</style>
