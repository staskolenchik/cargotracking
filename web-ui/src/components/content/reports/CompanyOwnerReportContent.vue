<template>
  <div>
    <md-card-content>
      <md-datepicker
        :class="getValidationClass('initialDate')"
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
        :class="getValidationClass('finalDate')"
        v-model="finalDate"
        md-immediately
      >
        <label>Final date</label>
        <span
          class="md-error"
          v-if="!$v.finalDate.required"
        >
          {{errors.FIELD_IS_REQUIRED}}
        </span>
      </md-datepicker>
    </md-card-content>

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
        Generate report
      </md-button>
    </md-card-actions>

    <div id="chart" />
  </div>
</template>

<script>
  import {required} from 'vuelidate/lib/validators';
  import Highcharts from 'highcharts';
  import moment from 'moment';
  import Excel from 'exceljs';

  import {Url} from '../../../constants/url';
  import {Errors} from '../../../constants/errors';

  export default {
    name: 'CompanyOwnerReportContent',

    data: () => ({
      initialDate: null,
      finalDate: null,
      chart: null,
      statistic: [],
      drivers: [],

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

      downloadExcel() {
        const workbook = new Excel.Workbook();
        const profitSheet = workbook.addWorksheet('Profit report');

        profitSheet.columns = [
          {header: 'Initial date', key: 'initDate', width: 15},
          {header: 'Final date', key: 'finalDate', width: 15},
          {header: 'Consumption', key: 'cons', width: 15},
          {header: 'Income', key: 'inc', width: 10},
          {header: 'Profit', key: 'prof', width: 10}
        ];
        profitSheet.getRow(1).fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: {
            argb: 'ADD5AF'
          }
        };

        for (let i = 0; i < this.statistic.length; i++) {
          profitSheet.addRow({
            initDate: this.statistic[i].startWeekDate,
            finalDate: this.statistic[i].endWeekDate,
            cons: this.statistic[i].consumption,
            inc: this.statistic[i].income,
            prof: this.statistic[i].profit
          });
        }

        const driversSheet = workbook.addWorksheet('Best drivers report');
        driversSheet.columns = [
          {header: 'Driver full name', key: 'driverFN', width: 40},
          {header: 'Profit', key: 'prof', width: 10}
        ];
        driversSheet.getRow(1).fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: {
            argb: '66AAEE'
          }
        };

        for (let i = 0; i < this.drivers.length; i++) {
          driversSheet.addRow({
            driverFN: this.drivers[i].driverFullName,
            prof: this.drivers[i].profit
          });
        }

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
          this.drawChart();
        }
      },

      drawChart() {
        this.sending = true;
        this.$http.get(Url.REPORT + '/client', {
          params: {
            initialDate: moment(this.initialDate).format('YYYY-MM-DD'),
            finalDate: moment(this.finalDate).format('YYYY-MM-DD')
          },
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(response => {
            this.statistic = response.body.statistic;
            this.drivers = response.body.bestFiveDrivers;

            let series = [];
            for (let i = 0; i < response.body.statistic.length; i++) {
              const data = [
                response.body.statistic[i].consumption,
                response.body.statistic[i].income,
                response.body.statistic[i].profit
              ];
              series.push({
                name: response.body.statistic[i].startWeekDate,
                data: data
              });
            }
            this.chart = Highcharts.chart('chart', {
              chart: {
                type: 'bar'
              },
              title: {
                text: 'Company profit'
              },
              xAxis: {
                categories: ['Consumption', 'Income', 'Profit']
              },
              yAxis: {
                title: {
                  text: 'Cost'
                }
              },
              series: series
            });

            this.sending = false;
          });
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('COMPANY_OWNER')) {
        this.$router.replace('/');
      }

      this.initialDate = new Date();
      this.finalDate = new Date();
      this.initialDate.setMonth(this.initialDate.getMonth() - 1);

      this.drawChart();
    }
  };
</script>

<style scoped>
  #chart {
    width:100%;
    height:400px;
  }
</style>
