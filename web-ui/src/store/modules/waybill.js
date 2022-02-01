import Vue from 'vue';
import moment from 'moment';

import {Url} from '../../constants/url';

const state = {
  items: [],
  totalElements: 0,
  data: {
    invoice: {
      id: null,
      number: null,
      storageAddress: null,
      productOwnerAddress: null,
      load: null
    },
    car: null,
    arrivalDate: null,
    arrivalTime: '00:00',
    checkpoints: []
  },
  sort: null,
  sending: false,
  waybillSaved: false,
  hasError: false,
  errorMessage: null,
  invalidArrivalDate: false,

  checkpointAdding: false,
  checkpointUpdating: false,

  // eslint-disable-next-line
  directionsService: new google.maps.DirectionsService(),

  hideMainForm: false,
  showCars: false
};

const getters = {
  getCarInfo: state => {
    if (state.data.car) {
      const item = state.data.car;
      return `Number: ${item.number}`;
    } else {
      return null;
    }
  },

  getInvoiceNumber: state => {
    if (state.data.invoice) {
      const item = state.data.invoice;
      return item.number;
    }
  }
};

const actions = {
  pageWaybillChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize
    };
    if (state.sort){
      pageAttr.sort = state.sort;
    }

    Vue.http.get(Url.WAYBILL + '{?statuses*}', {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const waybills = [];
        for (const item of response.data.content) {
          const status = item.status.charAt(0) + item.status.slice(1).replace('_', ' ').toLowerCase();

          const waybill = {
            id: item.id,
            sender: item.sender,
            receiver: item.receiver,
            invoiceNumber: item.invoiceNumber,
            carNumber: item.carNumber,
            startDate: item.startDate.substring(0, 10),
            status: status
          };
          waybills.push(waybill);
        }
        commit('setWaybills', waybills);
        commit('setTotalElements', response.data.totalElements);
      });
  },

  saveWaybill({commit}) {
    /* eslint-disable no-undef */
    const request = {
        origin: state.data.invoice.productOwnerAddress,
        destination: state.data.invoice.storageAddress,
        waypoints: state.data.checkpoints.map(item => ({location: item.address})),
        travelMode: google.maps.DirectionsTravelMode.DRIVING
    };

    state.directionsService.route(request, (response, status) => {
      if (status === google.maps.DirectionsStatus.OK) {
        let distance = 0;
        for (let i = 0; i < response.routes[0].legs.length; i++) {
          distance += response.routes[0].legs[i].distance.value;
        }

        /* eslint-enable no-undef */
        const dateAndTime = dateIntoString(state.data.arrivalDate) + 'T' + state.data.arrivalTime;
        const checkpointsToSend = [];
        for (let i = 0; i < state.data.checkpoints.length; i++) {
          let checkpoint = {
            address: state.data.checkpoints[i].address,
            requiredArrivalDate: state.data.checkpoints[i].requiredArrivalDate + 'T' + state.data.checkpoints[i].requiredArrivalTime
          };
          checkpointsToSend.push(checkpoint);
        }
        const form = {
          invoiceId: state.data.invoice.id,
          carId: state.data.car.id,
          endDate: dateAndTime,
          checkpoints: checkpointsToSend,
          distance: distance
        };
        commit('setSending', true);
        Vue.http.post(Url.WAYBILL, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            commit('setWaybillSaved', true);
            commit('setSending', false);
            commit('setHasError', false);
            commit('removeData');
          }, response => {
            commit('setSending', false);
            commit('setHasError', true);
            commit('setErrorMessage', response.body.errors[0]);
          });
      }
    });
  }
};

const mutations = {
  setWaybills(state, waybills) {
    state.items = waybills;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  setSort(state, sort) {
    state.sort = sort;
  },

  setCheckpointAdding(state, booleanValue) {
    state.checkpointAdding = booleanValue;
  },

  setCheckpointUpdating(state, booleanValue) {
    state.checkpointUpdating = booleanValue;
  },

  setSending(state, booleanValue) {
    state.sending = booleanValue;
  },

  setWaybillSaved(state, booleanValue) {
    state.waybillSaved = booleanValue;
  },

  setHasError(state, booleanValue) {
    state.hasError = booleanValue;
  },

  setErrorMessage(state, errorMessage) {
    state.errorMessage = errorMessage;
  },

  setInvalidArrivalDate(state, booleanValue) {
    state.invalidArrivalDate = booleanValue;
  },

  setArrivalDate(state, arrivalDate) {
    state.data.arrivalDate = arrivalDate;
  },

  updateArrivalTime(state, arrivalTime) {
    state.data.arrivalTime = arrivalTime;
  },

  updateDataCar(state, car) {
    state.data.car = car;
  },

  addDataCheckpoint(state, checkpoint) {
    state.data.checkpoints.push(checkpoint);
  },

  setUpdatingDataCheckpoint(state, id) {
    for (let i = 0; i < state.data.checkpoints.length; i++) {
      if (state.data.checkpoints[i].id === id) {
        let checkpoint = state.data.checkpoints[i];
        checkpoint.id = null;
        state.data.checkpoints.splice(i, 1, checkpoint);
        return;
      }
    }
  },

  removeData(state) {
    state.data.invoice = {};
    state.data.car = null;
    state.data.arrivalDate = null;
    state.data.checkpoints = [];
  },

  pushDataCheckpoint(state, checkpoint) {
    if (state.data.checkpoints.length === 1) {
      checkpoint.id = 1;
    } else {
      checkpoint.id = state.data.checkpoints[state.data.checkpoints.length - 2].id + 1;
    }
    state.data.checkpoints.splice(state.data.checkpoints.length - 1, 1, checkpoint);
  },

  updateDataCheckpoint(state, checkpoint) {
    for (let i = 0; i < state.data.checkpoints.length; i++) {
      if (state.data.checkpoints[i].id === null) {
        state.data.checkpoints.splice(i, 1, checkpoint);
        return;
      }
    }
  },

  updateArrivalDate(state, arrivalDate) {
    state.data.arrivalDate = arrivalDate;
  },

  deleteDataCheckpoint(state, checkpointIds) {
    state.data.checkpoints = state.data.checkpoints.filter(function(value) {
      return !checkpointIds.includes(value.id);
    });
  },

  showCarsTable(state) {
    state.hideMainForm = true;
    state.showCars = true;
  },

  showMainForm(state) {
    state.hideMainForm = false;
    state.showCars = false;
  },

  updateInvoice(state, invoice) {
    state.data.invoice = invoice;
  }
};

function dateIntoString(date) {
  return date ? moment(date).format('YYYY-MM-DD') : null;
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
