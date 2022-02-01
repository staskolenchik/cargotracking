<template>
  <div>
    <div v-if="userRoles.includes('DISPATCHER')">
      <product-owner-list v-if="showProductOwners"/>
      <storage-list v-if="showStorages"/>
      <driver-list v-if="showDrivers"/>
    </div>

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
              :disabled="sending ||
              (userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              (userRoles.includes('COMPANY_OWNER') && !userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              invoiceStatus === 'DELIVERED'"
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
              :disabled="sending ||
              (userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              (userRoles.includes('COMPANY_OWNER') && !userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              invoiceStatus === 'DELIVERED'"
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
              :disabled="sending ||
              (userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              (userRoles.includes('COMPANY_OWNER') && !userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              invoiceStatus === 'DELIVERED'"
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
              :disabled="sending ||
              (userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              (userRoles.includes('COMPANY_OWNER') && !userRoles.includes('MANAGER') && !userRoles.includes('DISPATCHER')) ||
              invoiceStatus === 'DELIVERED'"
              @click="showDriversTable"
            />
            <span
              class="md-error"
              v-if="!$v.getDriverInfo.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <product-list
            :delivered="invoiceStatus === 'DELIVERED'"
          />
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

          <product-writeoff-list
            v-if="userRoles.includes('MANAGER') && invoiceStatus === 'DELIVERED'"
          />

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
            v-if="userRoles.includes('DISPATCHER') && invoiceStatus !== 'DELIVERED'"
            type="submit"
            class="md-primary"
            :disabled="sending"
          >
            Edit
          </md-button>
          <md-button
            v-if="userRoles.includes('MANAGER') && invoiceStatus !== 'DELIVERED'"
            class="md-primary"
            :disabled="sending"
            @click="openWaybillCreationForm"
          >
            Create waybill
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
        :md-active.sync="invoiceUpdated"
      >
        {{messages.INVOICE_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {mapState, mapGetters} from 'vuex';
  import {required, maxLength} from 'vuelidate/lib/validators';

  import ProductList from './components/ProductList';
  import ProductWriteoffList from './components/ProductWriteoffList';
  import ProductOwnerList from './components/ProductOwnerList';
  import StorageList from './components/StorageList';
  import DriverList from './components/DriverList';
  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'InvoiceEditContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,
      userRoles: [],
      invoiceStatus: null
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
      invoiceUpdated: {
        get() {
          return this.$store.state.invoice.invoiceUpdated;
        },
        set(value) {
          this.$store.commit('invoice/setInvoiceUpdated', value);
        }
      }
    },

    methods: {
      openWaybillCreationForm() {
        this.$router.push({name: 'create-waybill', params: {id: this.$route.params.id}});
      },

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
          this.updateInvoice();
        }
      },

      updateInvoice() {
        this.$store.dispatch('invoice/updateInvoice', this.$route.params.id);
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
      if (!userRoles || !userRoles.includes('DISPATCHER') &&
          !userRoles.includes('COMPANY_OWNER') && !userRoles.includes('MANAGER')
      ) {
        this.$router.replace('/');
      }

      this.$store.commit('invoice/setInvoiceUpdated', false);
      this.$store.commit('invoice/setSending', false);
      this.$store.commit('invoice/setHasError', false);

      this.$http.get(`${Url.INVOICE}/${this.$route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          this.$store.commit('invoice/updateDataNumber', response.body.number);
          this.$store.commit('invoice/updateDataProductOwner', response.body.productOwner);
          this.$store.commit('invoice/updateDataStorage', response.body.storage);
          this.$store.commit('invoice/updateDataDriver', response.body.driver);
          this.$store.commit('invoice/updateDataProducts', response.body.products);

          this.invoiceStatus = response.body.status;
        }, response => {
          this.$store.commit('invoice/setHasError', true);
          this.$store.commit('invoice/setErrorMessage', response.body.errors[0]);
        });

      this.userRoles = JSON.parse(localStorage.getItem('roles'));

      if (this.userRoles.includes('MANAGER')) {
        this.$store.dispatch('invoice/getProductWriteoffs', this.$route.params.id);
      }
    },

    components: {ProductOwnerList, StorageList, DriverList, ProductList, ProductWriteoffList}
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
