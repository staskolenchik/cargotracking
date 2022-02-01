import Vue from 'vue';

const state = {
  items: [],
  totalElements: 0,
  filter: {
    name: null,
    status: []
  },
  sort: null
};

const actions = {
  pageClientChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      name: state.filter.name
    };
    if (state.sort) {
      pageAttr.sort = state.sort;
    }
    if (state.filter.status.length !== 0) {
      pageAttr.status = state.filter.status;
    }
    Vue.http.get('/api/clients{?status*}', {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const clients = [];
        for (const item of response.data.content) {
          const status = item.status.charAt(0) + item.status.slice(1).toLowerCase();
          const client = {
            id: item.id,
            name: item.name,
            status: status,
            deleteDate: item.deleteDate
          };
          clients.push(client);
        }
        commit('setClients', clients);
        commit('setTotalElements', response.data.totalElements);
      });
  }
};

const mutations = {
  setClients(state, clients) {
    state.items = clients;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  setSort(state, sort){
    state.sort = sort;
  },

  removeFilter(state) {
    state.filter.name = null;
    state.filter.status = [];
  },

  updateName(state, name) {
    state.filter.name = name;
  },

  updateStatus(state, status) {
    state.filter.status = status;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
};
