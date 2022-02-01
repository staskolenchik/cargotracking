<template>
  <div>
    <product-owner-list v-if="showProductOwners"/>
    <storage-list v-if="showStorages"/>
    <driver-list v-if="showDrivers"/>

    <form
      novalidate
      @submit.prevent="validateInvoice"
      v-if="!hideMainForm"
    >

      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('number')">
            <label>Number</label>
            <md-input
              v-model="number"
              name="number"
              :disabled="sending"
              :maxlength="fieldsLength.INVOICE_NUMBER"
            />
            <span
              class="md-error"
              v-if="!$v.number.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.number.maxlength"
            >
              {{errors.MAX_LENGTH(fieldsLength.INVOICE_NUMBER)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('getProductOwnerInfo')">
            <label>Product owner</label>
            <md-input
              :value="getProductOwnerInfo"
              class="c-pointer"
              readonly
              :disabled="sending"
              @click="showProductOwnersTable"
            />
            <span
              class="md-error"
              v-if="!$v.getProductOwnerInfo.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('getStorageInfo')">
            <label>Storage</label>
            <md-input
              :value="getStorageInfo"
              readonly
              class="c-pointer"
              :disabled="sending"
              @click="showStoragesTable"
            />
            <span
              class="md-error"
              v-if="!$v.getStorageInfo.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('getDriverInfo')">
            <label>Driver</label>
            <md-input
              :value="getDriverInfo"
              readonly
              class="c-pointer"
              :disabled="sending"
              @click="showDriversTable"
            />
            <span
              class="md-error"
              v-if="!$v.getDriverInfo.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <product-list :delivered="false"/>
          <md-field
            :class="getValidationClass('products')"
            class="n-after"
          >
            <span
              class="md-error"
              v-if="!$v.products.required || productAdding || productUpdating"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>

        <md-card-actions>
          <md-button
            class="md-primary"
            :disabled="sending"
            to="/invoices"
          >
            Back
          </md-button>

          <md-button
            type="submit"
            class="md-primary"
            :disabled="sending"
          >
            Save
          </md-button>
        </md-card-actions>

        <md-chip
          class="md-accent md-layout md-alignment-center"
          v-if="hasError"
        >
          {{errorMessage}}
        </md-chip>

      </md-card>

      <md-snackbar
        :md-active.sync="invoiceSaved"
      >
        {{messages.INVOICE_SAVED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {mapState, mapGetters} from 'vuex';
  import {required, maxLength} from 'vuelidate/lib/validators';

  import ProductList from './components/ProductList';
  import ProductOwnerList from './components/ProductOwnerList';
  import StorageList from './components/StorageList';
  import DriverList from './components/DriverList';
  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';

  export default {
    name: 'InvoiceAddContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages
    }),

    validations: {
      number: {
        required,
        maxLength: maxLength(FieldsLength.INVOICE_NUMBER)
      },
      getProductOwnerInfo: {required},
      getStorageInfo: {required},
      getDriverInfo: {required},
      products: {required}
    },

    computed: {
      ...mapGetters('invoice', [
        'getProductOwnerInfo',
        'getStorageInfo',
        'getDriverInfo'
      ]),
      ...mapState({
        sending: state => state.invoice.sending,
        hasError: state => state.invoice.hasError,
        errorMessage: state => state.invoice.errorMessage,

        hideMainForm: state => state.invoice.hideMainForm,
        showProductOwners: state => state.invoice.showProductOwners,
        showStorages: state => state.invoice.showStorages,
        showDrivers: state => state.invoice.showDrivers,

        productAdding: state => state.invoice.data.productAdding,
        productUpdating: state => state.invoice.data.productUpdating,
        products: state => state.invoice.data.products
      }),
      number: {
        get() {
          return this.$store.state.invoice.data.number;
        },
        set(value) {
          this.$store.commit('invoice/updateDataNumber', value);
        }
      },
      invoiceSaved: {
        get() {
          return this.$store.state.invoice.invoiceSaved;
        },
        set(value) {
          this.$store.commit('invoice/setInvoiceSaved', value);
        }
      }
    },

    methods: {
      getValidationClass(fieldName) {
        const field = this.$v[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      validateInvoice() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.saveInvoice();
        }
      },

      saveInvoice() {
        this.$store.dispatch('invoice/saveInvoice');
        this.$v.$reset();
      },

      showProductOwnersTable() {
        this.$store.commit('invoice/showProductOwnersTable');
      },

      showStoragesTable() {
        this.$store.commit('invoice/showStoragesTable');
      },

      showDriversTable() {
        this.$store.commit('user/updateUserRoles', ['DRIVER']);
        this.$store.commit('invoice/showDriversTable');
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('DISPATCHER')) {
        this.$router.replace('/');
      }

      this.$store.commit('invoice/setInvoiceSaved', false);
      this.$store.commit('invoice/setSending', false);
      this.$store.commit('invoice/setHasError', false);
      this.$store.commit('invoice/removeData');
    },

    components: {ProductOwnerList, StorageList, DriverList, ProductList}
  };
</script>

<style scoped>
  .c-pointer {
    cursor: pointer;
  }
  .n-after:after {
    background-image: none;
    height: 0;
  }
</style>
