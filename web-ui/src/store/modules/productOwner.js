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
  pageProductOwnerChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      name: state.filter.name
    };
    if (state.sort){
      pageAttr.sort = state.sort;
    }
    Vue.http.get(Url.PRODUCT_OWNER, {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const productOwners = [];
        for (const item of response.data.content) {
          const productOwner = {
            id: item.id,
            name: item.name,
            address: item.address
          };
          productOwners.push(productOwner);
        }
        commit('setProductOwners', productOwners);
        commit('setTotalElements', response.data.totalElements);
      });
  }
};

const mutations = {
  setProductOwners(state, productOwners) {
    state.items = productOwners;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  removeFilter(state) {
    state.filter.name = null;
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
