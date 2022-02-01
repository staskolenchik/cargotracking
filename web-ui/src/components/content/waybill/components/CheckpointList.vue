<template>
  <div>
    <md-table
      :value="checkpoints"
      md-card
      @md-selected="onSelect"
    >
      <md-table-toolbar>
        <h1 class="md-title">Checkpoint List</h1>
        <md-button
          class="md-icon-button
          md-raised
          md-primary"
          @click="addCheckpoint"
        >
          <md-icon title="add checkpoint">add</md-icon>
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
        <md-table-cell md-label="Address">
          {{ item.address }}
          <md-autocomplete
            v-if="item.id == null"
            id="address"
            :class="getValidationClass('address')"
            v-model="address"
            :md-options="addresses"
            :maxlength="fieldsLength.CHECKPOINT_ADDRESS"
            md-dense
            @input.native="findPlaces"
          >
            <label>Address</label>
            <span
              class="md-error"
              v-if="!$v.address.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.address.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.CHECKPOINT_ADDRESS)}}
            </span>
            <span
              class="md-error"
              v-else-if="!addressValid"
            >
              {{errors.INVALID_ADDRESS}}
            </span>
          </md-autocomplete>
        </md-table-cell>

        <md-table-cell :md-label="getRequiredWaybillArrivalDate">
          {{ item.requiredArrivalDate }}
          <md-datepicker
            v-if="item.id == null"
            :class="getValidationClass('requiredArrivalDate')"
            v-model="requiredArrivalDate"
          >
            <label>Required arrival date</label>

            <span
              class="md-error"
              v-if="!$v.requiredArrivalDate.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-if="!$v.requiredArrivalDate.minValue && $v.requiredArrivalDate.required"
            >
              {{errors.MIN_DATE(moment(new Date()).format('YYYY-MM-DD'))}}
            </span>
          </md-datepicker>
        </md-table-cell>

        <md-table-cell md-label="Required Arrival Time (24H:MM)">
          {{ item.requiredArrivalTime }}
          <md-field
            v-if="item.id == null"
            class="time-field"
            :class="getValidationClass('requiredArrivalTime')"
          >
            <time-select
              v-model="requiredArrivalTime"
              :picker-options="timeOptions"
              placeholder="Arrival Time"
            >
            </time-select>
            <span
              class="md-error"
              v-if="!$v.requiredArrivalTime.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.requiredArrivalTime.maxlength"
            >
              {{errors.MAX_LENGTH(fieldsLength.ARRIVAL_TIME)}}
            </span>
          </md-field>
        </md-table-cell>

        <md-table-cell md-label="Operation" class="operation-column">
          <md-button
            v-if="item.id == null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="pushCheckpoint(item)"
          >
            <md-icon title="save checkpoint">save</md-icon>
          </md-button>
          <md-button
            v-if="item.id != null"
            class="md-icon-button
            md-dense
            md-primary"
            @click="getCheckpoint(item)"
          >
            <md-icon title="edit checkpoint">edit</md-icon>
          </md-button>
          <md-button
            class="md-icon-button
            md-dense
            md-primary"
            @click="showDeleteConfirmDialog(item.id)"
          >
            <md-icon title="delete checkpoint">delete</md-icon>
          </md-button>
        </md-table-cell>
      </md-table-row>

      <md-table-empty-state md-label="Add checkpoints" />
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

    <md-dialog-alert
      :md-active.sync="hasDeleted"
      md-content="Checkpoints has been deleted!"
      md-confirm-text="OK"
    />
    <div id="google-service"></div>
  </div>
</template>

<script>
  import {mapState} from 'vuex';
  import {required, maxLength} from 'vuelidate/lib/validators';
  import {TimeSelect} from 'element-ui';
  import 'element-ui/lib/theme-chalk/index.css';
  import moment from 'moment';

  import {Confirmation} from '../../../../constants/confirmation';
  import {FieldsValueBounds} from '../../../../constants/fieldsValueBounds';
  import {FieldsLength} from '../../../../constants/fieldsLength';
  import {FieldsType} from '../../../../constants/fieldsType';
  import {Errors} from '../../../../constants/errors';

  export default {
    name: 'CheckpointList',

    data: () => ({
      selectedRows: [],
      selectedIds: [],
      activeConfirmDialog: false,
      itemIdsToDelete: [],
      tittleConfirmDialog: 'Delete checkpoint',
      contentConfirmDialog: Confirmation.ITEM_DELETE,
      currentSort: 'id',
      currentSortOrder: 'asc',
      hasDeleted: false,
      moment: moment,

      fieldsLength: FieldsLength,
      errors: Errors,
      fieldsType: FieldsType,
      fieldsValueBounds: FieldsValueBounds,

      address: null,
      addresses: [],
      requiredArrivalDate: null,
      requiredArrivalTime: '00:00',
      updatedId: null,

      service: null,
      addressValid: true,

      timeOptions: {
        start: '00:00',
        step: '00:30',
        end: '23:30'
      }
    }),

    validations: {
      address: {
        required,
        maxLength: maxLength(FieldsLength.CHECKPOINT_ADDRESS)
      },
      requiredArrivalDate: {
        required,
        minValue: value => dateIntoString(value) > new Date().toISOString().substring(0, 10)
      },
      requiredArrivalTime: {
        required,
        maxLength: maxLength(FieldsLength.ARRIVAL_TIME)
      }
    },

    computed: {
      ...mapState({
        checkpoints: state => state.waybill.data.checkpoints,
        waybillArrivalDate: state => state.waybill.data.arrivalDate
      }),
      getRequiredWaybillArrivalDate() {
        return 'Required arrival date (must greater than ' + new Date().toISOString().substring(0, 10) + ')';
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

      findPlaces() {
        const request = {
          query: this.address,
          fields: ['name']
        };

        /* eslint-disable no-undef */
        this.service.findPlaceFromQuery(request, (results, status) => {
          this.addresses = [];
          if (status === google.maps.places.PlacesServiceStatus.OK) {
            for (let i = 0; i < results.length; i++) {
              this.addresses.push(results[i].name);
            }
          }
        });
        /* eslint-enable no-undef */
      },

      onDeleteConfirm() {
        this.removeSelection();
        this.$store.commit('waybill/deleteDataCheckpoint', this.itemIdsToDelete);
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
        return `${count} checkpoint${plural} selected`;
      },

      removeSelection() {
        this.selectedRows.splice(0, this.selectedRows.length);
        this.selectedIds = [];
      },

      addCheckpoint() {
        if (this.waybillArrivalDate) {
          if (dateIntoString(this.waybillArrivalDate) > new Date().toISOString().substring(0, 10)) {
            if (!this.$store.state.waybill.checkpointAdding && !this.$store.state.waybill.checkpointUpdating) {
              this.$store.commit('waybill/addDataCheckpoint', {});
              this.$store.commit('waybill/setCheckpointAdding', true);
              this.$store.commit('waybill/setInvalidArrivalDate', false);
            }
          }
        } else {
          this.$store.commit('waybill/setInvalidArrivalDate', true);
        }
      },

      pushCheckpoint() {
        this.$v.$touch();
        if (this.waybillArrivalDate && this.requiredArrivalDate) {
          const waybillArrivalDateInMs = this.waybillArrivalDate.getTime();
          const checkpointArrivalDateInMs = this.stringIntoDate(this.requiredArrivalDate).getTime();

          if (checkpointArrivalDateInMs > waybillArrivalDateInMs) {
            this.requiredArrivalDate = null;
            const field = this.$v['requiredArrivalDate'];
            return {
              'md-invalid': field.$invalid && field.$dirty
            };
          }

          if (!this.$v.$invalid) {
            const request = {
              query: this.address,
              fields: ['name']
            };

            /* eslint-disable no-undef */
            this.service.findPlaceFromQuery(request, (results, status) => {
              if (status !== google.maps.places.PlacesServiceStatus.OK || results.length !== 1) {
                this.addressValid = false;
                document.getElementById('address').classList.add('md-invalid');
              } else {
                document.getElementById('address').classList.remove('md-invalid');

                const checkpoint = {
                  address: this.address,
                  requiredArrivalDate: dateIntoString(this.requiredArrivalDate),
                  requiredArrivalTime: this.requiredArrivalTime
                };

                if (this.updatedId !== null) {
                  checkpoint.id = this.updatedId;
                  this.updatedId = null;
                  this.$store.commit('waybill/updateDataCheckpoint', checkpoint);
                  this.$store.commit('waybill/setCheckpointUpdating', false);
                } else {
                  this.$store.commit('waybill/pushDataCheckpoint', checkpoint);
                  this.$store.commit('waybill/setCheckpointAdding', false);
                }

                this.clearForm();
                this.$v.$reset();
                this.addressValid = true;
              }
            });
            /* eslint-enable no-undef */
          }
          this.$store.commit('waybill/setInvalidArrivalDate', false);
        } else {
          this.$store.commit('waybill/setInvalidArrivalDate', true);
        }
      },

      getCheckpoint(item) {
        if (!this.$store.state.waybill.checkpointAdding && !this.$store.state.waybill.checkpointUpdating) {
          this.updatedId = item.id;
          this.$store.commit('waybill/setUpdatingDataCheckpoint', item.id);
          this.$store.commit('waybill/setCheckpointAdding', true);
          this.address = item.address;
          this.requiredArrivalDate = item.requiredArrivalDate;
        } else {
          this.$v.$touch();
        }
      },

      clearForm() {
        this.address = null;
        this.requiredArrivalDate = null;
        this.requiredArrivalTime = '00:00';
      },

      stringIntoDate(string) {
        return string ? moment(string, 'YYYY-MM-DD').toDate() : null;
      }
    },

    mounted: function() {
      this.$store.commit('waybill/setCheckpointAdding', false);

      /* eslint-disable no-undef */
      const map = new google.maps.Map(document.getElementById('google-service'));
      this.service = new google.maps.places.PlacesService(map);
      /* eslint-enable no-undef */
    },

    components: {TimeSelect}
  };

  function dateIntoString(date) {
    return date ? moment(date).format('YYYY-MM-DD') : null;
  }
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

  .time-field:after {
    height: 0;
  }
</style>
