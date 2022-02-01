import Vue from 'vue';

import {Url} from '../../constants/url';

const state = {
  items: [],
  totalElements: 0,
  filter: {
    number: null,
    fuelConsumptionLess: null,
    fuelConsumptionMore: null,
    loadCapacityLess: null,
    loadCapacityMore: null,
    carTypes: []
  },
  sort: null
};

const actions = {
  pageCarChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      number: state.filter.number,
      fuelConsumptionLess: state.filter.fuelConsumptionLess,
      fuelConsumptionMore: state.filter.fuelConsumptionMore,
      loadCapacityLess: state.filter.loadCapacityLess,
      loadCapacityMore: state.filter.loadCapacityMore
    };
    if (state.sort){
      pageAttr.sort = state.sort;
    }
    if (state.filter.carTypes.length !== 0) {
      pageAttr.carTypes = state.filter.carTypes;
    }
    Vue.http.get(Url.CAR + '{?carTypes*}', {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const cars = [];
        for (const item of response.data.content) {
          const type = item.carType.charAt(0) + item.carType.slice(1).replace('_', ' ').toLowerCase();

          const car = {
            id: item.id,
            number: item.number,
            loadCapacity: item.loadCapacity,
            carType: type
          };
          cars.push(car);
        }
        commit('setCars', cars);
        commit('setTotalElements', response.data.totalElements);
      });
  }
};

const mutations = {
  setCars(state, cars) {
    state.items = cars;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  removeFilter(state) {
    state.filter.number = null;
    state.filter.fuelConsumptionLess = null;
    state.filter.fuelConsumptionMore = null;
    state.filter.loadCapacityLess = null;
    state.filter.loadCapacityMore = null;
    state.filter.carTypes = [];
  },

  setSort(state, sort) {
    state.sort = sort;
  },

  updateFilterNumber(state, number) {
    state.filter.number = number;
  },

  updateFilterFuelConsumptionLess(state, fuelConsumptionLess) {
    state.filter.fuelConsumptionLess = fuelConsumptionLess;
  },

  updateFilterFuelConsumptionMore(state, fuelConsumptionMore) {
    state.filter.fuelConsumptionMore = fuelConsumptionMore;
  },

  updateFilterLoadCapacityLess(state, loadCapacityLess) {
    state.filter.loadCapacityLess = loadCapacityLess;
  },

  updateFilterLoadCapacityMore(state, loadCapacityMore) {
    state.filter.loadCapacityMore = loadCapacityMore;
  },

  updateFilterCarTypes(state, carTypes) {
    state.filter.carTypes = carTypes;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
};
