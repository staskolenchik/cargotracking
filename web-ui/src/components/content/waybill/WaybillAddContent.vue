<template>
  <div>
    <car-list v-if="showCars"/>

    <form
      novalidate
      @submit.prevent="validateWaybill"
      v-if="!hideMainForm"
    >
      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('getInvoiceNumber')">
            <label>Invoice Number</label>
            <md-input
              :value="getInvoiceNumber"
              readonly
            />
          </md-field>

          <md-field :class="getValidationClass('getCarInfo')">
            <label>Car</label>
            <md-input
              :value="getCarInfo"
              class="c-pointer"
              :disabled="sending"
              readonly
              @click="showCarsTable"
            />
            <span
              class="md-error"
              v-if="!$v.getCarInfo.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <md-datepicker
            :class="getValidationClass('arrivalDate')"
            v-model="arrivalDate"
            :disabled="sending"
            @input="validateArrivalDate"
          >
            <label>Arrival Date*</label>
            <span
              class="md-error"
              v-if="!$v.arrivalDate.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.arrivalDate.minValue && $v.arrivalDate.required"
            >
              {{errors.MIN_DATE(moment(new Date()).format('YYYY-MM-DD'))}}
            </span>
            <span :class="validateWaybillWhenCheckpointAdd">
            </span>
          </md-datepicker>

          <md-field
            class="time-field"
            :class="getValidationClass('arrivalTime')"
          >
            <time-select
              :disabled="sending"
              v-model="arrivalTime"
              :picker-options="timeOptions"
              placeholder="Arrival Time"
            >
            </time-select>
            <span
              class="md-error"
              v-if="!$v.arrivalTime.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>

          <checkpoint-list/>

          <md-progress-bar md-mode="indeterminate" v-if="sending"/>

          <md-card-actions>
            <md-button
              class="md-accent"
              :disabled="sending"
              @click="goBack()"
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
        </md-card-content>
      </md-card>
    </form>
  </div>
</template>

<script>
  import {mapGetters, mapState} from 'vuex';
  import {required} from 'vuelidate/lib/validators';
  import {TimeSelect} from 'element-ui';
  import 'element-ui/lib/theme-chalk/index.css';
  import moment from 'moment';

  import CarList from './components/CarList';
  import CheckpointList from './components/CheckpointList';
  import {Messages} from '../../../constants/messages';
  import {Errors} from '../../../constants/errors';
  import {Url} from '../../../constants/url';

  export default {
    name: 'WaybillAddContent',

    data: () => ({
      messages: Messages,
      errors: Errors,
      moment: moment,
      timeOptions: {
        start: '00:00',
        step: '00:30',
        end: '23:30'
      }
    }),

    validations: {
      getCarInfo: {required},
      arrivalDate: {
        required,
        minValue: value => dateIntoString(value) > new Date().toISOString().substring(0, 10)
      },
      arrivalTime: {required}
    },

    computed: {
      ...mapGetters('waybill', [
        'getCarInfo', 'getInvoiceNumber'
      ]),
      ...mapState({
        sending: state => state.waybill.sending,
        hasError: state => state.waybill.hasError,
        errorMessage: state => state.waybill.errorMessage,
        invalidArrivalDate: state => state.waybill.invalidArrivalDate,

        hideMainForm: state => state.waybill.hideMainForm,
        showCars: state => state.waybill.showCars,

        checkpoints: state => state.waybill.data.checkpoints
      }),
      arrivalDate: {
        get() {
          return this.$store.state.waybill.data.arrivalDate;
        },
        set(value) {
          this.$store.commit('waybill/updateArrivalDate', value);
        }
      },
      arrivalTime: {
        get() {
          return this.$store.state.waybill.data.arrivalTime;
        },
        set(value) {
          this.$store.commit('waybill/updateArrivalTime', value);
        }
      }
    },

    methods: {
      goBack() {
        this.$router.push({name: 'get-invoice', params: {id: this.$route.params.id}});
      },

      getValidationClass(fieldName) {
        const field = this.$v[fieldName];
        if (field) {
          if (fieldName === 'arrivalDate') {
            this.$store.commit('waybill/setInvalidArrivalDate', true);
          }
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      validateWaybillWhenCheckpointAdd() {
        if (this.invalidArrivalDate) {
          this.$v.$touch();
        }
      },

      validateArrivalDate() {
        this.$v.$touch();
      },

      showCarsTable() {
        this.$store.commit('waybill/showCarsTable');
      },

      validateWaybill() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.saveWaybill();
        }
      },

      setArrivalDate() {
        this.$store.commit('waybill/setArrivalDate', this.arrivalDate);
      },

      saveWaybill() {
        this.setArrivalDate();
        this.$store.dispatch('waybill/saveWaybill');
        this.$router.push('/waybills');
        this.$v.$reset();
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('MANAGER')) {
        this.$router.replace('/');
      }

      this.$store.commit('waybill/removeData');
      this.$v.$reset();

      this.$http.get(`${Url.INVOICE}/${this.$route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          let load = 0;
          for (let i = 0; i < response.body.products.length; i++) {
            load += response.body.products[i].amount;
          }

          const invoice = {
            id: response.body.id,
            number: response.body.number,
            storageAddress: response.body.storage.address,
            productOwnerAddress: response.body.productOwner.address,
            load: load
          };

          this.$store.commit('waybill/updateInvoice', invoice);
        }, response => {
          this.$store.commit('waybill/setHasError', true);
          this.$store.commit('waybill/setErrorMessage', response.body.errors[0]);
        });
    },

    components: {CheckpointList, CarList, TimeSelect}
  };

  function dateIntoString(date) {
    return date ? moment(date).format('YYYY-MM-DD') : null;
  }
</script>

<style scoped>
  .c-pointer {
    cursor: pointer;
  }

  .time-area {
    margin-bottom: 25px;
  }

  .time-field:after {
    height: 0;
  }
</style>
