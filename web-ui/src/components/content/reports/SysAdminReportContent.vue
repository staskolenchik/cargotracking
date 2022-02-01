<template>
  <div>
    <md-card-content>
      <md-datepicker
        :class="getValidationClass"
        v-model="initialDate"
        md-immediately
        id="initialDate"
      >
        <label>Initial date</label>
        <span
          class="md-error"
          v-if="!$v.initialDate.required"
        >
          {{errors.FIELD_IS_REQUIRED}}
        </span>
        <span
          class="md-error"
          v-else-if="invalidInitialDate"
        >
          {{errors.MAX_DATE(moment(finalDate).format('YYYY-MM-DD'))}}
        </span>
      </md-datepicker>
      <md-datepicker
        :class="getValidationClass"
        v-model="finalDate"
        md-immediately
        id="finalDate"
      >
        <label>Final date</label>
        <span
          class="md-error"
          v-if="!$v.finalDate.required"
        >
          {{errors.FIELD_IS_REQUIRED}}
        </span>
      </md-datepicker>

      <md-card-actions>
        <md-button
          class="md-primary"
          :disabled="sending"
          @click="downloadExcel"
        >
          <md-icon>file_download</md-icon>
          Save as excel
        </md-button>
        <md-button
          class="md-primary"
          :disabled="sending"
          @click="validateForm"
        >
          Display statistics
        </md-button>
      </md-card-actions>

      <md-table>
        <md-table-toolbar>
          <h1 class="md-title">Statistics</h1>
        </md-table-toolbar>

        <md-table-row slot="md-table-row">
          <md-table-head>Parameter</md-table-head>
          <md-table-head>Value</md-table-head>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>From</md-table-cell>
          <md-table-cell>{{ statistics.startIntervalDate }}</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>To</md-table-cell>
          <md-table-cell>{{ statistics.endIntervalDate }}</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>Income</md-table-cell>
          <md-table-cell>{{ statistics.income }}$</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>Consumption</md-table-cell>
          <md-table-cell>{{ statistics.consumption }}$</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>Profit</md-table-cell>
          <md-table-cell>{{ statistics.profit }}$</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>Active clients</md-table-cell>
          <md-table-cell>{{ statistics.activeClients }}</md-table-cell>
        </md-table-row>

        <md-table-row slot="md-table-row">
          <md-table-cell>Lost clients</md-table-cell>
          <md-table-cell>{{ statistics.lostClients }}</md-table-cell>
        </md-table-row>
      </md-table>

    </md-card-content>
  </div>
</template>

<script>
  import {required} from 'vuelidate/lib/validators';
  import moment from 'moment';
  import Excel from 'exceljs';

  import {Url} from '../../../constants/url';
  import {Errors} from '../../../constants/errors';

  export default {
    name: 'SysAdminReport',

    data: () => ({
      initialDate: null,
      finalDate: null,
      statistics: [],

      errors: Errors,
      moment: moment,

      sending: false,
      invalidInitialDate: false
    }),

    validations: {
      initialDate: {required},
      finalDate: {required}
    },

    methods: {
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

      validateForm() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          if (this.initialDate > this.finalDate) {
            this.invalidInitialDate = true;
            document.getElementById('initialDate').classList.add('md-invalid');
            return;
          }

          this.invalidInitialDate = false;
          document.getElementById('initialDate').classList.remove('md-invalid');
          this.showStatistics();
        }
      },

      showStatistics() {
        this.sending = true;
        this.$http.get(Url.REPORT + '/sysadmin', {
          params: {
            initialDate: moment(this.initialDate).format('YYYY-MM-DD'),
            finalDate: moment(this.finalDate).format('YYYY-MM-DD')
          },
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(response => {
            this.statistics = response.body.statistics;

            let series = [];

            const data = [
              response.body.statistics.consumption,
              response.body.statistics.income,
              response.body.statistics.profit,
              response.body.statistics.activeClients,
              response.body.statistics.lostClients

            ];
            series.push({
              name: response.body.statistics,
              data: data
            });

          });
        this.sending = false;
      },

      downloadExcel() {
        const workbook = new Excel.Workbook();
        const profitSheet = workbook.addWorksheet('Statistics');

        profitSheet.columns = [
          {header: 'Parameter', key: 'param', width: 15},
          {header: 'Value', key: 'value', width: 15}
        ];
        profitSheet.getRow(1).fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: {
            argb: 'ADD5AF'
          }
        };

        profitSheet.addRow({
          param: 'Initial date',
          value: this.statistics.startIntervalDate
        });
        profitSheet.addRow({
          param: 'Final date',
          value: this.statistics.endIntervalDate
        });
        profitSheet.addRow({
          param: 'Consumption',
          value: this.statistics.consumption
        });
        profitSheet.addRow({
          param: 'Income',
          value: this.statistics.income
        });
        profitSheet.addRow({
          param: 'Profit',
          value: this.statistics.profit
        });
        profitSheet.addRow({
          param: 'Active clients',
          value: this.statistics.activeClients
        });
        profitSheet.addRow({
          param: 'Lost clients',
          value: this.statistics.lostClients
        });

        workbook.xlsx.writeBuffer()
          .then(buffer => {
            const a = document.createElement('a');
            document.body.appendChild(a);
            a.style = 'display: none';
            const blob = new Blob([buffer]);
            const url = window.URL.createObjectURL(blob);
            a.href = url;
            a.download = moment(new Date).format('YYYY-MM-DD') + 'report.xlsx';
            a.click();
            window.URL.revokeObjectURL(url);
          });
      }
    },

    mounted: function () {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('SYS_ADMIN')) {
        this.$router.replace('/');
      }
      this.initialDate = new Date();
      this.finalDate = new Date();
      this.initialDate.setMonth(this.initialDate.getMonth() - 1);

      this.showStatistics();
    }
  };
</script>

