import Vue from 'vue';

import {Url} from '../../constants/url';

const state = {
  items: [],
  totalElements: 0,
  filter: {
    name: null
  },
  sort: 'name_str,asc'
};

const actions = {
  pageStorageChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      name: state.filter.name
    };
    if (state.sort) {
      pageAttr.sort = state.sort;
    }
    Vue.http.get(Url.STORAGE, {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const storages = [];
        for (const item of response.data.content) {
          const storage = {
            id: item.id,
            name: item.name,
            address: item.address
          };
          storages.push(storage);
        }
        commit('setStorages', storages);
        commit('setTotalElements', response.data.totalElements);
      });
  }
};

const mutations = {
  setStorages(state, storages) {
    state.items = storages;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  setSort(state, sort) {
    state.sort = sort;
  },

  updateFilterName(state, name) {
    state.filter.name = name;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
};
