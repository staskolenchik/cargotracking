<template>
  <div>
    <md-table
      :value="products"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Product List</h1>
        <md-button
          v-if="userRoles.includes('DISPATCHER') && !delivered"
          class="md-icon-button
          md-raised
          md-primary"
          @click="addProduct"
        >
          <md-icon title="add product">add</md-icon>
        </md-button>
      </md-table-toolbar>

      <md-table-toolbar
        v-if="userRoles.includes('DISPATCHER') && !delivered"
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
        :md-disabled="!userRoles.includes('DISPATCHER') || delivered"
      >
        <md-table-cell md-label="Name">
          {{ item.name }}
          <md-field
            v-if="item.id == null"
            :class="getValidationClass('name')"
          >
            <label>Name</label>
            <md-input
              v-model="name"
              :maxlength="fieldsLength.PRODUCT_NAME"
            />
            <span
              class="md-error"
              v-if="!$v.name.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.name.maxlength"
            >
              {{errors.MAX_LENGTH(fieldsLength.PRODUCT_NAME)}}
            </span>
          </md-field>
        </md-table-cell>

        <md-table-cell md-label="Amount">
          {{ item.amount }}
          <md-field
            v-if="item.id == null"
            :class="getValidationClass('amount')"
          >
            <label>Amount</label>
            <md-input
              v-model="amount"
            />
            <span
              class="md-error"
              v-if="!$v.amount.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.amount.integer"
            >
              {{errors.INCORRECT_TYPE(fieldsType.PRODUCT_AMOUNT)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.amount.between"
            >
              {{errors.INCORRECT_VALUE(fieldsValueBounds.CAR_FUEL_CONSUMPTION.min, fieldsValueBounds.CAR_FUEL_CONSUMPTION.max)}}
            </span>
          </md-field>
        </md-table-cell>

        <md-table-cell
          v-if="userRoles.includes('DISPATCHER') && !delivered"
          md-label="Operation"
          class="operation-column"
        >
          <md-button
            v-if="item.id == null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="pushProduct()"
          >
            <md-icon title="save product">save</md-icon>
          </md-button>
          <md-button
            v-if="item.id != null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="getProduct(item)"
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

        <md-table-cell
          md-label="Operation"
          class="operation-column"
          v-if="delivered && userRoles.includes('MANAGER')"
        >
          <md-button
            class="md-icon-button
            md-dense md-primary"
            @click="writeoffProduct(item)"
          >
            <md-icon title="writeoff product">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="No products found" />
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

    <md-dialog :md-active="activeWriteoffDialog" v-if="delivered">
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
        <md-button class="md-primary" @click="onWriteoffConfirm">Writeoff</md-button>
      </md-dialog-actions>
    </md-dialog>

    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Products has been deleted!"
      md-confirm-text="OK"
    />

    <md-dialog-alert
      :md-active.sync="hasWriteoffed"
      :md-content="responseMessage"
      md-confirm-text="OK"
    />
  </div>
</template>

<script>
  import {mapState} from 'vuex';
  import {required, maxLength, between, integer} from 'vuelidate/lib/validators';

  import {Confirmation} from '../../../../constants/confirmation';
  import {FieldsValueBounds} from '../../../../constants/fieldsValueBounds';
  import {FieldsLength} from '../../../../constants/fieldsLength';
  import {FieldsType} from '../../../../constants/fieldsType';
  import {Errors} from '../../../../constants/errors';
  import {Url} from '../../../../constants/url';

  export default {
    name: 'ProductList',

    props: {
      delivered: Boolean
    },

    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete product',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      hasDeleted: false,

      fieldsLength: FieldsLength,
      errors: Errors,
      fieldsType: FieldsType,
      fieldsValueBounds: FieldsValueBounds,

      name: null,
      amount: null,
      updatedId: null,

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
      name: {
        required,
        maxLength: maxLength(FieldsLength.PRODUCT_NAME)
      },
      amount: {
        required,
        integer,
        between: between(FieldsValueBounds.PRODUCT_AMOUNT.min, FieldsValueBounds.PRODUCT_AMOUNT.max)
      },
      writeoffAmount: {required, integer},
      writeoffStatus: {required}
    },

    methods: {
      writeoffProduct(item) {
        this.writeoffItem = item;
        this.activeWriteoffDialog = true;
      },

      onWriteoffConfirm() {
        this.name = 'q';
        this.amount = '1';

        if (!this.amountBetween) {
          document.getElementById('amount').classList.add('md-invalid');
          return;
        }
        this.$v.$touch();
        if (this.$v.$invalid || !this.amountBetween) {
          return;
        }

        this.clearForm();
        this.activeWriteoffDialog = false;

        const form = {
          productId: this.writeoffItem.id,
          amount: this.writeoffAmount,
          status: this.writeoffStatus
        };

        this.$http.post(Url.PRODUCT_WRITEOFF, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.responseMessage = 'Product has been writeoffed!';
            this.hasWriteoffed = true;
            this.$store.commit('invoice/changeProductCount', {
              id: this.writeoffItem.id,
              amount: this.writeoffItem.amount - parseInt(this.writeoffAmount, 10)
            });
            this.$store.dispatch('invoice/getProductWriteoffs', this.$route.params.id);
            this.clearWriteoffForm();
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
        this.$store.commit('invoice/deleteDataProduct', this.itemIdsToDelete);
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
        return `${count} product${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      addProduct() {
        if (!this.$store.state.invoice.productAdding && !this.$store.state.invoice.productUpdating) {
          this.$store.commit('invoice/addDataProduct', {});
          this.$store.commit('invoice/setProductAdding', true);
        } else {
          this.$v.$touch();
        }
      },

      pushProduct() {
        this.writeoffAmount = 1;
        this.writeoffStatus = 'q';

        this.$v.$touch();
        if (!this.$v.$invalid) {
          let product = {
            name: this.name,
            amount: this.amount
          };
          if (this.updatedId !== null) {
            product.id = this.updatedId;
            this.updatedId = null;
            this.$store.commit('invoice/updateDataProduct', product);
            this.$store.commit('invoice/setProductUpdating', false);
          } else {
            this.$store.commit('invoice/pushDataProduct', product);
            this.$store.commit('invoice/setProductAdding', false);
          }
          this.clearForm();
          this.$v.$reset();
        }
      },

      getProduct(item) {
        if (!this.$store.state.invoice.productAdding && !this.$store.state.invoice.productUpdating) {
          this.updatedId = item.id;
          this.$store.commit('invoice/setUpdatingDataProduct', item.id);
          this.$store.commit('invoice/setProductUpdating', true);
          this.name = item.name;
          this.amount = item.amount;
        } else {
          this.$v.$touch();
        }
      },

      clearForm() {
        this.name = null;
        this.amount = null;
      }
    },

    computed: {
      ...mapState({
        products: state => state.invoice.data.products
      }),
      amountBetween: function() {
        if (this.writeoffAmount < 1 || this.writeoffAmount > this.writeoffItem.amount) {
          document.getElementById('amount').classList.add('md-invalid');
          return false;
        } else {
          document.getElementById('amount').classList.remove('md-invalid');
          return true;
        }
      }
    },

    mounted: function() {
      this.$store.commit('invoice/setProductAdding', false);
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

  .md-field.md-disabled:after {
    background-image: none;
    height: 0;
  }

  .md-field.md-disabled .md-count {
    display: none;
  }
</style>
