<template>
  <div>
    <md-table
      :value="productWriteoffs"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Product Writeoff List</h1>
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
        slot-scope="{ item }"
        md-selectable="multiple"
        :md-disabled="!userRoles.includes('MANAGER')"
      >
        <md-table-cell md-label="Name">
          {{ item.name }}
        </md-table-cell>

        <md-table-cell md-label="Amount">
          {{ item.amount }}
        </md-table-cell>

        <md-table-cell md-label="Status">
          {{ item.status.charAt(0).toUpperCase() + item.status.slice(1).toLowerCase() }}
        </md-table-cell>

        <md-table-cell
          v-if="userRoles.includes('MANAGER')"
          md-label="Operation"
          class="operation-column"
        >
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="getWriteoff(item)"
          >
            <md-icon title="edit product">edit</md-icon>
          </md-button>
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete product">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No product writeoffs found" />
    </md-table>

    <md-dialog-confirm
      :md-active.sync="activeConfirmDialog"
      :md-title="tittleConfirmDialog"
      :md-content="contentConfirmDialog"
      md-confirm-text="Delete"
      md-cancel-text="Cancel"
      @md-cancel="onCancel"
      @md-confirm="onDeleteConfirm"
    />

    <md-dialog :md-active="activeWriteoffDialog">
      <md-dialog-title>{{ tittleWriteoffDialog }}</md-dialog-title>

      <md-dialog-content>
        <md-field
          id="amount"
          :class="getValidationClass('writeoffAmount')"
        >
          <label>Amount</label>
          <md-input v-model="writeoffAmount" />
          <span
            class="md-error"
            v-if="!$v.writeoffAmount.required"
          >
            {{errors.FIELD_IS_REQUIRED}}
          </span>
          <span
            class="md-error"
            v-else-if="!$v.writeoffAmount.integer"
          >
            {{errors.INCORRECT_TYPE(fieldsType.PRODUCT_AMOUNT)}}
          </span>
          <span
            class="md-error"
            v-else-if="!amountBetween"
          >
            {{errors.INCORRECT_VALUE(0, writeoffItem.amount + 1)}}
          </span>
        </md-field>

        <md-field :class="getValidationClass('writeoffStatus')">
          <label>Product status</label>
          <md-select v-model="writeoffStatus">
            <md-option value="LOST">Lost</md-option>
            <md-option value="SPOILED">Spoiled</md-option>
            <md-option value="STOLEN">Stolen</md-option>
          </md-select>
          <span
            class="md-error"
            v-if="!$v.writeoffStatus.required"
          >
            {{errors.FIELD_IS_REQUIRED}}
          </span>
        </md-field>
      </md-dialog-content>

      <md-dialog-actions>
        <md-button class="md-primary" @click="onWriteoffCancel">Cancel</md-button>
        <md-button class="md-primary" @click="onWriteoffConfirm">Edit</md-button>
      </md-dialog-actions>
    </md-dialog>

    <md-dialog-alert
      :md-active.sync="hasWriteoffed"
      :md-content="responseMessage"
      md-confirm-text="OK"
    />

    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Product writeoffs has been deleted!"
      md-confirm-text="OK"
    />
  </div>
</template>

<script>
  import {mapState} from 'vuex';
  import {required, integer} from 'vuelidate/lib/validators';

  import {Confirmation} from '../../../../constants/confirmation';
  import {FieldsValueBounds} from '../../../../constants/fieldsValueBounds';
  import {FieldsLength} from '../../../../constants/fieldsLength';
  import {FieldsType} from '../../../../constants/fieldsType';
  import {Errors} from '../../../../constants/errors';
  import {Url} from '../../../../constants/url';

  export default {
    name: 'ProductWriteoffList',

    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete product writeoff',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      hasDeleted: false,

      fieldsLength: FieldsLength,
      errors: Errors,
      fieldsType: FieldsType,
      fieldsValueBounds: FieldsValueBounds,

      userRoles: [],

      writeoffItem: null,
      writeoffAmount: null,
      writeoffStatus: null,
      activeWriteoffDialog: false,
      tittleWriteoffDialog: 'Writeoff product',
      hasWriteoffed: false,
      responseMessage: null
    }),

    validations: {
      writeoffAmount: {required, integer},
      writeoffStatus: {required}
    },

    methods: {
      getWriteoff(item) {
        const product = this.$store.state.invoice.data.products.filter(product => product.id === item.productId);
        this.writeoffItem = Object.assign({}, item);
        this.writeoffItem.amount += product[0].amount;
        this.writeoffAmount = item.amount;
        this.writeoffStatus = item.status;
        this.activeWriteoffDialog = true;
      },

      onWriteoffConfirm() {
        if (!this.amountBetween) {
          document.getElementById('amount').classList.add('md-invalid');
          return;
        }
        this.$v.$touch();
        if (this.$v.$invalid || !this.amountBetween) {
          return;
        }

        this.activeWriteoffDialog = false;

        const form = {
          amount: this.writeoffAmount,
          status: this.writeoffStatus
        };

        this.$http.put(`${Url.PRODUCT_WRITEOFF}/${this.writeoffItem.id}`, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.responseMessage = 'Product writeoff has been updated!';
            this.hasWriteoffed = true;
            this.$store.commit('invoice/changeProductCount', {
              id: this.writeoffItem.productId,
              amount: this.writeoffItem.amount - parseInt(this.writeoffAmount, 10)
            });
            this.clearWriteoffForm();
            this.$store.dispatch('invoice/getProductWriteoffs', this.$route.params.id);
          }, () => {
            this.responseMessage = 'Something went wrong!';
            this.hasWriteoffed = true;
          });

        this.$v.$reset();
      },

      clearWriteoffForm() {
        this.writeoffItem = null;
        this.writeoffAmount = null;
        this.writeoffStatus = null;
      },

      onWriteoffCancel() {
        this.$v.$reset();
        this.activeWriteoffDialog = false;
        this.clearWriteoffForm();
      },

      getValidationClass(fieldName) {
        const field = this.$v[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      onDeleteConfirm() {
        this.removeSelection();
        const itemIdsToDelete = this.itemIdsToDelete;
        this.$http.delete(Url.PRODUCT_WRITEOFF, {
          body: itemIdsToDelete,
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.hasDeleted = true;
            this.$store.commit('invoice/deleteDataProductWriteoff', itemIdsToDelete);
            this.$http.get(`${Url.INVOICE}/${this.$route.params.id}`, {
              headers: {
                Authorization: `Bearer ${localStorage.accessToken}`
              }
            })
              .then(response => {
                this.$store.commit('invoice/updateDataProducts', response.body.products);
              }, response => {
                this.$store.commit('invoice/setHasError', true);
                this.$store.commit('invoice/setErrorMessage', response.body.errors[0]);
              });
          });
        this.itemIdsToDelete = [];
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
        return `${count} product writeoff${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      }
    },

    computed: {
      ...mapState({
        productWriteoffs: state => state.invoice.data.productWriteoffs
      }),
      amountBetween: function() {
        if (this.writeoffAmount < 1 || this.writeoffAmount > this.writeoffItem.amount) {
          document.getElementById('amount').classList.add('md-invalid');
          return false;
        } else {
          if (document.getElementById('amount')) {
            document.getElementById('amount').classList.remove('md-invalid');
          }
          return true;
        }
      }
    },

    mounted: function() {
      this.userRoles = JSON.parse(localStorage.getItem('roles'));
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
</style>
